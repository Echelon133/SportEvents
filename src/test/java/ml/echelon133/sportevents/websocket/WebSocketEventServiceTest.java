package ml.echelon133.sportevents.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.event.types.GoalEvent;
import ml.echelon133.sportevents.event.types.StandardEvent;
import ml.echelon133.sportevents.match.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static ml.echelon133.sportevents.TestUtils.getRandomMatch;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketEventServiceTest {

    private final static int PORT = 8080;
    private final static String URL = "ws://localhost:" + PORT + "/sport-events";
    private final static MessageConverter CONVERTER;
    private final static String TESTED_DESTINATION = "/matches/1";

    private CompletableFuture<AbstractMatchEvent> completableEvent;

    @Autowired
    private WebSocketEventServiceImpl webSocketEventService;

    // Use a static block instead of a @BeforeClass method, because the CONVERTER field is final
    static {
        // Setup object mapper with a mix-in that knows how to convert received events to correct subclasses of AbstractMatchEvent
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.addMixIn(AbstractMatchEvent.class, TestEventMixIn.class);
        CONVERTER = new MappingJackson2MessageConverter();
        ((MappingJackson2MessageConverter) CONVERTER).setObjectMapper(oMapper);
    }


    private List<Transport> buildTestTransportClient() {
        return Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
    }

    @Before
    public void setup() throws InterruptedException, ExecutionException, TimeoutException {
        completableEvent = new CompletableFuture<>();

        // Standard stomp client, except that we need custom message converter to make event polymorphism work
        WebSocketStompClient client = new WebSocketStompClient(
                new SockJsClient(
                        buildTestTransportClient()
                )
        );
        client.setMessageConverter(CONVERTER);

        StompSession stompSession = client.connect(URL, new StompSessionHandlerAdapter() {}).get(2, TimeUnit.SECONDS);
        stompSession.subscribe(TESTED_DESTINATION, new EventStompFrameHandler());
    }

    @Test
    public void sendEventOverWebSocketCorrectlySendsOutStandardEvent() throws InterruptedException,
                                                                              ExecutionException,
                                                                              TimeoutException {
        Match match = getRandomMatch();
        AbstractMatchEvent standardEvent = new StandardEvent(1L, "Test msg",
                AbstractMatchEvent.EventType.STANDARD_DESCRIPTION, match);


        // When
        webSocketEventService.sendEventOverWebSocket(TESTED_DESTINATION, standardEvent);
        StandardEvent receivedEvent = (StandardEvent) completableEvent.get(1, TimeUnit.SECONDS);

        // Then
        assertThat(receivedEvent.getType()).isEqualTo(standardEvent.getType());
        assertThat(receivedEvent.getTime()).isEqualTo(standardEvent.getTime());
        assertThat(receivedEvent.getMessage()).isEqualTo(standardEvent.getMessage());
    }

    @Test
    public void sendEventOverWebSocketCorrectlySendsOutGoalEvent() throws InterruptedException, ExecutionException, TimeoutException {
        Match match = getRandomMatch();
        GoalEvent goalEvent = new GoalEvent(1L, "Test msg",
                AbstractMatchEvent.EventType.GOAL, match, match.getTeamA(),"Test player");


        // When
        webSocketEventService.sendEventOverWebSocket(TESTED_DESTINATION, goalEvent);
        GoalEvent receivedEvent = (GoalEvent) completableEvent.get(1, TimeUnit.SECONDS);

        // Then
        assertThat(receivedEvent.getType()).isEqualTo(goalEvent.getType());
        assertThat(receivedEvent.getTime()).isEqualTo(goalEvent.getTime());
        assertThat(receivedEvent.getMessage()).isEqualTo(goalEvent.getMessage());
        assertThat(receivedEvent.getTeamScoring().getId()).isEqualTo(goalEvent.getTeamScoring().getId());
        assertThat(receivedEvent.getTeamScoring().getName()).isEqualTo(goalEvent.getTeamScoring().getName());
        assertThat(receivedEvent.getPlayerScoring()).isEqualTo(goalEvent.getPlayerScoring());
    }


    private class EventStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            // All messages that we will handle are subclasses of AbstractMatchEvent
            return AbstractMatchEvent.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            // Manually set our event to the payload of this frame
            completableEvent.complete((AbstractMatchEvent) payload);
        }
    }
}
