package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.*;
import ml.echelon133.sportevents.event.types.dto.*;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import ml.echelon133.sportevents.team.Team;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
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
        teamA.setId(5L);

        Team teamB = new Team("testB", league);
        teamB.setId(10L);

        Match match = new Match(new Date(), teamA, teamB, league, null);
        match.setId(1L);
        return match;
    }

    private List<AbstractMatchEvent> getTestEventsForMatch(Match match) {
        AbstractMatchEvent startFirstHalfEvent = new ManagingEvent(1L, "Test", AbstractMatchEvent.EventType.START_FIRST_HALF, match);
        AbstractMatchEvent finishFirstHalfEvent = new ManagingEvent(45L, "Test", AbstractMatchEvent.EventType.FINISH_FIRST_HALF, match);
        AbstractMatchEvent startSecondHalfEvent = new ManagingEvent(45L, "Test", AbstractMatchEvent.EventType.START_SECOND_HALF, match);
        AbstractMatchEvent finishSecondHalfEvent = new ManagingEvent(90L, "Test", AbstractMatchEvent.EventType.FINISH_SECOND_HALF, match);
        AbstractMatchEvent finishMatchEvent = new ManagingEvent(90L, "Test", AbstractMatchEvent.EventType.FINISH_MATCH, match);
        AbstractMatchEvent startOTFirstHalfEvent = new ManagingEvent(90L, "Test", AbstractMatchEvent.EventType.START_OT_FIRST_HALF, match);
        AbstractMatchEvent finishOTFirstHalfEvent = new ManagingEvent(105L, "Test", AbstractMatchEvent.EventType.FINISH_OT_FIRST_HALF, match);
        AbstractMatchEvent startOTSecondHalfEvent = new ManagingEvent(105L, "Test", AbstractMatchEvent.EventType.START_OT_SECOND_HALF, match);
        AbstractMatchEvent finishOTSecondHalfEvent = new ManagingEvent(120L, "Test", AbstractMatchEvent.EventType.FINISH_OT_SECOND_HALF, match);
        AbstractMatchEvent goalEvent = new GoalEvent(10L, "Test", AbstractMatchEvent.EventType.GOAL, match, match.getTeamA(), "Test player");
        AbstractMatchEvent penaltyEvent = new PenaltyEvent(120L, "Test", AbstractMatchEvent.EventType.PENALTY, match, match.getTeamB());
        AbstractMatchEvent cardEvent = new CardEvent(20L, "Test", AbstractMatchEvent.EventType.CARD, match, "Player", CardEvent.CardColor.YELLOW);
        AbstractMatchEvent substitutionEvent = new SubstitutionEvent(45L, "Test", AbstractMatchEvent.EventType.SUBSTITUTION, match, "Player One", "Player Two");
        AbstractMatchEvent standardDescriptionEvent = new StandardEvent(10L, "Test", AbstractMatchEvent.EventType.STANDARD_DESCRIPTION, match);

        List<AbstractMatchEvent> matchEvents = Arrays.asList(startFirstHalfEvent, finishFirstHalfEvent,
                startSecondHalfEvent, finishSecondHalfEvent, finishMatchEvent,
                startOTFirstHalfEvent, finishOTFirstHalfEvent, startOTSecondHalfEvent, finishOTSecondHalfEvent, goalEvent,
                penaltyEvent, cardEvent, substitutionEvent, standardDescriptionEvent);

        return matchEvents;
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

    @Test(expected = ProcessedEventRejectedException.class)
    public void convertEventDtoToEntityFailsWhenGoalEventDtoHasInvalidTeamId() throws Exception {
        Match match = getTestMatch();
        GoalEventDto matchEventDto = new GoalEventDto(10L, "Test message", "GOAL",
                6L, "Player test");

        // When
        GoalEvent goalEvent = (GoalEvent)eventService.convertEventDtoToEntity(matchEventDto, match);
    }

    @Test
    public void convertEventDtoToEntityConvertsGoalEventDtoToCorrectEntity() throws Exception {
        Match match = getTestMatch();
        GoalEventDto matchEventDto = new GoalEventDto(10L, "Test message", "GOAL",
                                             5L, "Player test");

        // When
        GoalEvent goalEvent = (GoalEvent)eventService.convertEventDtoToEntity(matchEventDto, match);

        // Then
        assertThat(goalEvent.getId()).isNull();
        assertThat(goalEvent.getTime()).isEqualTo(matchEventDto.getTime());
        assertThat(goalEvent.getMessage()).isEqualTo(matchEventDto.getMessage());
        assertThat(goalEvent.getType()).isEqualTo(AbstractMatchEvent.EventType.GOAL);
        assertThat(goalEvent.getMatch()).isEqualTo(match);
        assertThat(goalEvent.getTeamScoring()).isEqualTo(match.getTeamA());
        assertThat(goalEvent.getPlayerScoring()).isEqualTo(matchEventDto.getScorerName());
    }

    @Test(expected = ProcessedEventRejectedException.class)
    public void convertEventDtoToEntityFailsWhenPenaltyEventDtoHasInvalidTeamId() throws Exception {
        Match match = getTestMatch();
        PenaltyEventDto matchEventDto = new PenaltyEventDto(10L, "Test message", "PENALTY", 11L);

        // When
        PenaltyEvent penaltyEvent = (PenaltyEvent)eventService.convertEventDtoToEntity(matchEventDto, match);
    }

    @Test
    public void convertEventDtoToEntityConvertsPenaltyEventDtoToCorrectEntity() throws Exception {
        Match match = getTestMatch();
        PenaltyEventDto matchEventDto = new PenaltyEventDto(10L, "Test message", "PENALTY", 10L);

        // When
        PenaltyEvent penaltyEvent = (PenaltyEvent)eventService.convertEventDtoToEntity(matchEventDto, match);

        // Then
        assertThat(penaltyEvent.getId()).isNull();
        assertThat(penaltyEvent.getTime()).isEqualTo(matchEventDto.getTime());
        assertThat(penaltyEvent.getMessage()).isEqualTo(matchEventDto.getMessage());
        assertThat(penaltyEvent.getType()).isEqualTo(AbstractMatchEvent.EventType.PENALTY);
        assertThat(penaltyEvent.getMatch()).isEqualTo(match);
        assertThat(penaltyEvent.getTeam()).isEqualTo(match.getTeamB());
    }

    @Test
    public void convertEventDtoToEntityConvertsCardEventDtoToCorrectEntity() throws Exception {
        Match match = getTestMatch();
        CardEventDto matchEventDto = new CardEventDto(10L, "Test message", "CARD", "Player name" ,"RED");

        // When
        CardEvent cardEvent = (CardEvent)eventService.convertEventDtoToEntity(matchEventDto, match);

        // Then
        assertThat(cardEvent.getId()).isNull();
        assertThat(cardEvent.getTime()).isEqualTo(matchEventDto.getTime());
        assertThat(cardEvent.getMessage()).isEqualTo(matchEventDto.getMessage());
        assertThat(cardEvent.getType()).isEqualTo(AbstractMatchEvent.EventType.CARD);
        assertThat(cardEvent.getMatch()).isEqualTo(match);
        assertThat(cardEvent.getCardedPlayer()).isEqualTo(matchEventDto.getPlayer());
        assertThat(cardEvent.getCardColor()).isEqualTo(CardEvent.CardColor.RED);
    }

    private Set<AbstractMatchEvent.EventType> extractEventTypeSetFromEventList(List<AbstractMatchEvent> events) {
        return events
                .stream()
                .map(AbstractMatchEvent::getType)
                .collect(Collectors.toSet());
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_NotStarted() throws Exception {
        // Test match has NOT_STARTED status by default
        Match match = getTestMatch();

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.NOT_STARTED);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(2);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.START_FIRST_HALF)).isTrue();
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_FirstHalf() throws Exception {
        Match match = getTestMatch();
        match.setStatus(Match.Status.FIRST_HALF);

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.FIRST_HALF);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(5);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.FINISH_FIRST_HALF)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.GOAL)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.SUBSTITUTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.CARD)).isTrue();
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_SecondHalf() throws Exception {
        Match match = getTestMatch();
        match.setStatus(Match.Status.SECOND_HALF);

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.SECOND_HALF);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(6);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.FINISH_SECOND_HALF)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.GOAL)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.SUBSTITUTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.CARD)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.FINISH_MATCH)).isTrue();
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_BreakTime() throws Exception {
        Match match = getTestMatch();
        match.setStatus(Match.Status.BREAK_TIME);

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.BREAK_TIME);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(5);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.START_SECOND_HALF)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.START_OT_SECOND_HALF)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.START_OT_FIRST_HALF)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.SUBSTITUTION)).isTrue();
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_OTFirstHalf() throws Exception {
        Match match = getTestMatch();
        match.setStatus(Match.Status.OT_FIRST_HALF);

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.OT_FIRST_HALF);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(5);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.FINISH_OT_FIRST_HALF)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.SUBSTITUTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.GOAL)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.CARD)).isTrue();
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_OTSecondHalf() throws Exception {
        Match match = getTestMatch();
        match.setStatus(Match.Status.OT_SECOND_HALF);

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.OT_SECOND_HALF);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(6);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.FINISH_OT_SECOND_HALF)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.FINISH_MATCH)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.SUBSTITUTION)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.GOAL)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.CARD)).isTrue();
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_Penalties() throws Exception {
        Match match = getTestMatch();
        match.setStatus(Match.Status.PENALTIES);

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.PENALTIES);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(2);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.FINISH_MATCH)).isTrue();
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.PENALTY)).isTrue();
    }

    @Test
    public void processEventAcceptsCorrectEventsWhenMatchStatusIs_Finished() throws Exception {
        Match match = getTestMatch();
        match.setStatus(Match.Status.FINISHED);

        List<AbstractMatchEvent> acceptedEvents = new ArrayList<>();

        // When
        for (AbstractMatchEvent event : getTestEventsForMatch(match)) {
            try {
                eventService.processEvent(event);
                acceptedEvents.add(event);
            } catch (ProcessedEventRejectedException ex) {
                // do nothing, we only want to collect accepted events
            } finally {
                // restore tested status that might have been changed by processEvent call
                match.setStatus(Match.Status.FINISHED);
            }
        }

        // Then
        Set<AbstractMatchEvent.EventType> acceptedEventTypes = extractEventTypeSetFromEventList(acceptedEvents);

        assertThat(acceptedEvents.size()).isEqualTo(1);
        assertThat(acceptedEventTypes.contains(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION)).isTrue();
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_StartFirstHalf() throws Exception {
        Match testMatch = getTestMatch();

        AbstractMatchEvent matchEvent = new ManagingEvent(1L, "Test",
                                                          AbstractMatchEvent.EventType.START_FIRST_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.FIRST_HALF);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_FinishFirstHalf() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.FIRST_HALF);

        AbstractMatchEvent matchEvent = new ManagingEvent(45L, "Test",
                                                          AbstractMatchEvent.EventType.FINISH_FIRST_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.BREAK_TIME);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_StartSecondHalf() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.BREAK_TIME);

        AbstractMatchEvent matchEvent = new ManagingEvent(45L, "Test",
                                                          AbstractMatchEvent.EventType.START_SECOND_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.SECOND_HALF);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_FinishMatch() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.SECOND_HALF);

        AbstractMatchEvent matchEvent = new ManagingEvent(90L, "Test",
                                                          AbstractMatchEvent.EventType.FINISH_MATCH, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.FINISHED);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_FinishSecondHalf() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.SECOND_HALF);

        AbstractMatchEvent matchEvent = new ManagingEvent(90L, "Test",
                                                          AbstractMatchEvent.EventType.FINISH_SECOND_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.BREAK_TIME);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_StartOTFirstHalf() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.BREAK_TIME);

        AbstractMatchEvent matchEvent = new ManagingEvent(90L, "Test",
                                                          AbstractMatchEvent.EventType.START_OT_FIRST_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.OT_FIRST_HALF);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_FinishOTFirstHalf() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.OT_FIRST_HALF);

        AbstractMatchEvent matchEvent = new ManagingEvent(105L, "Test",
                                                          AbstractMatchEvent.EventType.FINISH_OT_FIRST_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.BREAK_TIME);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_StartOTSecondHalf() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.BREAK_TIME);

        AbstractMatchEvent matchEvent = new ManagingEvent(105L, "Test",
                                                          AbstractMatchEvent.EventType.START_OT_SECOND_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.OT_SECOND_HALF);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_FinishOTSecondHalf() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.OT_SECOND_HALF);

        AbstractMatchEvent matchEvent = new ManagingEvent(120L, "Test",
                                                          AbstractMatchEvent.EventType.FINISH_OT_SECOND_HALF, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.PENALTIES);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_FinishMatch_WhenInPenalties() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.PENALTIES);

        AbstractMatchEvent matchEvent = new ManagingEvent(120L, "Test",
                                                          AbstractMatchEvent.EventType.FINISH_MATCH, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.FINISHED);
    }

    @Test
    public void processEventMatchStatusChangesAfterSendingEvent_FinishMatch_WhenInOT() throws Exception {
        Match testMatch = getTestMatch();
        testMatch.setStatus(Match.Status.OT_SECOND_HALF);

        AbstractMatchEvent matchEvent = new ManagingEvent(120L, "Test",
                                                          AbstractMatchEvent.EventType.FINISH_MATCH, testMatch);

        // When
        eventService.processEvent(matchEvent);

        // Then
        assertThat(testMatch.getStatus()).isEqualTo(Match.Status.FINISHED);
    }
}
