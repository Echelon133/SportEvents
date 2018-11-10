package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.event.types.ManagingEvent;
import ml.echelon133.sportevents.event.types.StandardEvent;
import ml.echelon133.sportevents.event.types.dto.MatchEventDto;
import ml.echelon133.sportevents.event.types.dto.StandardEventDto;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock
    private MatchService matchService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Match getTestMatch() {
        League league = new League("test name", "test country");
        Team teamA = new Team("testA", league);
        Team teamB = new Team("testB", league);
        Match match = new Match(new Date(), teamA, teamB, league, null);
        match.setId(1L);
        return match;
    }

    @Test(expected = ProcessedEventRejectedException.class)
    public void processEventRejectsAllEvents() throws Exception {
        Match match = getTestMatch();
        AbstractMatchEvent event = new ManagingEvent(10L, "Test message",
                AbstractMatchEvent.EventType.START_FIRST_HALF, match);

        // When
        eventService.processEvent(event);
    }

    @Test
    public void convertEventDtoToEntityConvertsStandardEventDtoToCorrectEntity() throws Exception {
        Match match = getTestMatch();
        MatchEventDto matchEventDto = new StandardEventDto(10L, "Test message", "STANDARD_DESCRIPTION");

        // When
        AbstractMatchEvent matchEvent = eventService.convertEventDtoToEntity(matchEventDto, match);

        // Then
        assertThat(matchEvent.getId()).isNull();
        assertThat(matchEvent.getTime()).isEqualTo(matchEventDto.getTime());
        assertThat(matchEvent.getMessage()).isEqualTo(matchEventDto.getMessage());
        assertThat(matchEvent.getType()).isEqualTo(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION);
        assertThat(matchEvent.getMatch()).isEqualTo(match);
        assertThat(matchEvent).isExactlyInstanceOf(StandardEvent.class);
    }
}
