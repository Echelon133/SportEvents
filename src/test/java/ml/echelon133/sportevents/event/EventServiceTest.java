package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.event.types.ManagingEvent;
import ml.echelon133.sportevents.event.types.StandardEvent;
import ml.echelon133.sportevents.event.types.SubstitutionEvent;
import ml.echelon133.sportevents.event.types.dto.ManagingEventDto;
import ml.echelon133.sportevents.event.types.dto.MatchEventDto;
import ml.echelon133.sportevents.event.types.dto.StandardEventDto;
import ml.echelon133.sportevents.event.types.dto.SubstitutionEventDto;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import ml.echelon133.sportevents.team.Team;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    public void convertEventDtoToEntityConvertsManagingEventDtoToCorrectEntity() throws Exception {
        Match match = getTestMatch();

        // Dto that has a type that is in this list should be converted to an entity that is an instance of ManagingEvent
        List<String> allTestedTypesAsString = Arrays.asList("START_FIRST_HALF", "FINISH_FIRST_HALF",
                                                            "START_SECOND_HALF", "FINISH_SECOND_HALF",
                                                            "START_OT_FIRST_HALF", "FINISH_OT_FIRST_HALF",
                                                            "START_OT_SECOND_HALF", "FINISH_OT_SECOND_HALF",
                                                            "FINISH_MATCH");

        // We mostly care about checking the correctness of type conversion.
        // Time and message can stay the same
        List<MatchEventDto> matchEventDtos = allTestedTypesAsString
                .stream()
                .map(s -> new ManagingEventDto(10L, "Some message", s))
                .collect(Collectors.toList());

        for (MatchEventDto eventDto : matchEventDtos) {
            // Each eventDto has a different type (one of the types in a list of strings above).
            // Still, all of these types should result in receiving an entity that is an instance of ManagingEvent
            AbstractMatchEvent.EventType expectedType = AbstractMatchEvent.EventType.valueOf(eventDto.getType());

            // When
            AbstractMatchEvent matchEvent = eventService.convertEventDtoToEntity(eventDto, match);

            // Then
            assertThat(matchEvent.getId()).isNull();
            assertThat(matchEvent.getTime()).isEqualTo(eventDto.getTime());
            assertThat(matchEvent.getMessage()).isEqualTo(eventDto.getMessage());
            assertThat(matchEvent.getType()).isEqualTo(expectedType);
            assertThat(matchEvent.getMatch()).isEqualTo(match);
            assertThat(matchEvent).isExactlyInstanceOf(ManagingEvent.class);
        }
    }

    @Test
    public void convertEventDtoToEntityConvertsSubstitutionEventDtoToCorrectEntity() throws Exception {
        Match match = getTestMatch();
        SubstitutionEventDto matchEventDto = new SubstitutionEventDto(10L, "Test message", "SUBSTITUTION",
                                                    "Test Player", "Test Player2");

        // When
        SubstitutionEvent substitutionEvent = (SubstitutionEvent)eventService.convertEventDtoToEntity(matchEventDto, match);

        // Then
        assertThat(substitutionEvent.getId()).isNull();
        assertThat(substitutionEvent.getTime()).isEqualTo(matchEventDto.getTime());
        assertThat(substitutionEvent.getMessage()).isEqualTo(matchEventDto.getMessage());
        assertThat(substitutionEvent.getType()).isEqualTo(AbstractMatchEvent.EventType.SUBSTITUTION);
        assertThat(substitutionEvent.getMatch()).isEqualTo(match);
        assertThat(substitutionEvent.getPlayerIn()).isEqualTo(matchEventDto.getPlayerIn());
        assertThat(substitutionEvent.getPlayerOut()).isEqualTo(matchEventDto.getPlayerOut());
    }
}
