package ml.echelon133.sportevents.event;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.event.types.*;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.team.Team;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private EventController eventController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    private Match buildMatch(Long id, Team teamA, Team teamB, League league, Stadium stadium) {
        Match testMatch = new Match(new Date(), teamA, teamB, league, stadium);
        testMatch.setId(id);
        return testMatch;
    }

    @Test
    public void getEventsReturnsEmptyResourcesCorrectly() throws Exception {
        Match match = buildMatch(10L, null, null, null, null);

        // Given
        given(matchService.findById(10L)).willReturn(match);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches/10/events"))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void getEventsReturnsAllEventsCorrectlyDeserialized() throws Exception {
        Match match = buildMatch(1L, null, null, null, null);

        ManagingEvent event1 = new ManagingEvent(1L, "Test msg",
                AbstractMatchEvent.EventType.START_FIRST_HALF, match);
        StandardEvent event2 = new StandardEvent(5L, "Another msg",
                AbstractMatchEvent.EventType.STANDARD_DESCRIPTION, match);
        GoalEvent event3 = new GoalEvent(10L, "Goal",
                AbstractMatchEvent.EventType.GOAL, match, null, "Some player");
        CardEvent event4 = new CardEvent(22L, "test",
                AbstractMatchEvent.EventType.CARD, match, "Test player", CardEvent.CardColor.YELLOW);
        event1.setId(1L);
        event2.setId(2L);
        event3.setId(3L);
        event4.setId(4L);

        List<AbstractMatchEvent> events = Arrays.asList(event1, event2, event3, event4);
        match.setEvents(events);

        // Given
        given(matchService.findById(1L)).willReturn(match);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches/1/events"))
                .andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$[0].id").toString()).isEqualTo(event1.getId().toString());
        assertThat(json.read("$[0].time").toString()).isEqualTo(event1.getTime().toString());
        assertThat(json.read("$[0].message").toString()).isEqualTo(event1.getMessage());
        assertThat(json.read("$[0].type").toString()).isEqualTo(event1.getType().toString());

        assertThat(json.read("$[1].id").toString()).isEqualTo(event2.getId().toString());
        assertThat(json.read("$[1].time").toString()).isEqualTo(event2.getTime().toString());
        assertThat(json.read("$[1].message").toString()).isEqualTo(event2.getMessage());
        assertThat(json.read("$[1].type").toString()).isEqualTo(event2.getType().toString());

        assertThat(json.read("$[2].id").toString()).isEqualTo(event3.getId().toString());
        assertThat(json.read("$[2].time").toString()).isEqualTo(event3.getTime().toString());
        assertThat(json.read("$[2].message").toString()).isEqualTo(event3.getMessage());
        assertThat(json.read("$[2].type").toString()).isEqualTo(event3.getType().toString());
        assertThat(json.read("$[2].playerScoring").toString()).isEqualTo(event3.getPlayerScoring());

        assertThat(json.read("$[3].id").toString()).isEqualTo(event4.getId().toString());
        assertThat(json.read("$[3].time").toString()).isEqualTo(event4.getTime().toString());
        assertThat(json.read("$[3].message").toString()).isEqualTo(event4.getMessage());
        assertThat(json.read("$[3].type").toString()).isEqualTo(event4.getType().toString());
        assertThat(json.read("$[3].cardedPlayer").toString()).isEqualTo(event4.getCardedPlayer());
        assertThat(json.read("$[3].cardColor").toString()).isEqualTo(event4.getCardColor().toString());
    }
}
