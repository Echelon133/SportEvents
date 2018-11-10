package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.event.types.ManagingEvent;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import ml.echelon133.sportevents.team.Team;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock
    private MatchService matchService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test(expected = ProcessedEventRejectedException.class)
    public void processEventRejectsAllEvents() throws Exception {
        League league = new League("test name", "test country");
        Team teamA = new Team("testA", league);
        Team teamB = new Team("testB", league);
        Match match = new Match(new Date(),teamA, teamB, league, null);
        AbstractMatchEvent event = new ManagingEvent(10L, "Test message",
                AbstractMatchEvent.EventType.START_FIRST_HALF, match);

        // When
        eventService.processEvent(event);
    }
}
