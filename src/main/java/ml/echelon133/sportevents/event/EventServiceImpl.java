package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.*;
import ml.echelon133.sportevents.event.types.dto.*;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import ml.echelon133.sportevents.team.Team;
import ml.echelon133.sportevents.websocket.WebSocketEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventServiceImpl implements EventService {

    private MatchService matchService;
    private WebSocketEventService webSocketEventService;

    @Autowired
    public EventServiceImpl(MatchService matchService, WebSocketEventService webSocketEventService) {
        this.matchService = matchService;
        this.webSocketEventService = webSocketEventService;
    }

    private Team extractScoringTeamWithId(Long teamId, Match eventMatch) throws ProcessedEventRejectedException {
        // If there is a match between TeamA and TeamB and the client sends an event in which he informs, that
        // ex. a goal was scored by a team with ID that is not an ID of TeamA nor TeamB, this event should be rejected
        List<Team> teams = Arrays.asList(eventMatch.getTeamA(), eventMatch.getTeamB());

        Optional<Team> team = teams.stream().filter(t -> t.getId().longValue() == teamId).findFirst();
        if (team.isPresent()) {
            return team.get();
        } else {
            String formattedMessage = String.format("None of the teams playing in this match have ID equal to %d", teamId);
            throw new ProcessedEventRejectedException(formattedMessage);
        }
    }

    @Override
    public AbstractMatchEvent convertEventDtoToEntity(MatchEventDto eventDto, Match eventMatch) throws ProcessedEventRejectedException {
        AbstractMatchEvent entityEvent;
        AbstractMatchEvent.EventType eventType = AbstractMatchEvent.EventType.valueOf(eventDto.getType());

        switch (eventType.toString()) {
            case "STANDARD_DESCRIPTION":
                entityEvent = new StandardEvent(eventDto.getTime(), eventDto.getMessage(), eventType, eventMatch);
                break;
            case "START_FIRST_HALF":
            case "FINISH_FIRST_HALF":
            case "START_SECOND_HALF":
            case "FINISH_SECOND_HALF":
            case "START_OT_FIRST_HALF":
            case "FINISH_OT_FIRST_HALF":
            case "START_OT_SECOND_HALF":
            case "FINISH_OT_SECOND_HALF":
            case "FINISH_MATCH":
                entityEvent = new ManagingEvent(eventDto.getTime(), eventDto.getMessage(), eventType, eventMatch);
                break;
            case "SUBSTITUTION":
                SubstitutionEventDto subEventDto = (SubstitutionEventDto)eventDto;
                entityEvent = new SubstitutionEvent(subEventDto.getTime(), subEventDto.getMessage(), eventType, eventMatch,
                                                    subEventDto.getPlayerIn(), subEventDto.getPlayerOut());
                break;
            case "GOAL":
                GoalEventDto goalEventDto = (GoalEventDto)eventDto;
                Team teamScoringGoal = extractScoringTeamWithId(goalEventDto.getTeamId(), eventMatch);
                entityEvent = new GoalEvent(goalEventDto.getTime(), goalEventDto.getMessage(), eventType, eventMatch,
                                            teamScoringGoal, goalEventDto.getScorerName());
                break;
            case "PENALTY":
                PenaltyEventDto penaltyEventDto = (PenaltyEventDto)eventDto;
                Team teamScoringPenalty = extractScoringTeamWithId(penaltyEventDto.getTeamId(), eventMatch);
                entityEvent = new PenaltyEvent(penaltyEventDto.getTime(), penaltyEventDto.getMessage(), eventType, eventMatch,
                                               teamScoringPenalty);
                break;
            case "CARD":
                CardEventDto cardEventDto = (CardEventDto)eventDto;
                CardEvent.CardColor cardColor = CardEvent.CardColor.valueOf(cardEventDto.getColor());
                entityEvent = new CardEvent(cardEventDto.getTime(), cardEventDto.getMessage(), eventType, eventMatch,
                                            cardEventDto.getPlayer(), cardColor);
                break;
            default:
                // this will never be executed, because:
                // 1) EventType.valueOf in the beginning of the method will fail first
                // 2) Cases above catch all current event types
                // this code is here only for syntactic purposes
                throw new ProcessedEventRejectedException("Event with this type cannot be processed");
        }
        return entityEvent;
    }

    @Override
    public boolean processEvent(AbstractMatchEvent event) throws ProcessedEventRejectedException {
        boolean processedAndSaved = false;
        Match match = event.getMatch();

        if (isValidEventTypeForStatus(match.getStatus(), event.getType())) {
            processedAndSaved = continueEventProcessing(event);
            webSocketEventService.sendEventOverWebSocket(match.getWebsocketPath(), event);
        } else {
            String message = String
                    .format("Cannot send events of type %s when match status is %s%n", event.getType().toString(),
                                                                                       match.getStatus().toString());
            throw new ProcessedEventRejectedException(message);
        }
        return processedAndSaved;
    }

    private boolean isValidEventTypeForStatus(Match.Status status, AbstractMatchEvent.EventType type) {
        List<AbstractMatchEvent.EventType> allowedEventTypes;

        switch (status.toString()) {
            case "NOT_STARTED":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.START_FIRST_HALF,
                                                  AbstractMatchEvent.EventType.STANDARD_DESCRIPTION);
                break;
            case "FIRST_HALF":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.GOAL,
                                                  AbstractMatchEvent.EventType.STANDARD_DESCRIPTION,
                                                  AbstractMatchEvent.EventType.CARD,
                                                  AbstractMatchEvent.EventType.FINISH_FIRST_HALF,
                                                  AbstractMatchEvent.EventType.SUBSTITUTION);
                break;
            case "SECOND_HALF":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.GOAL,
                                                  AbstractMatchEvent.EventType.STANDARD_DESCRIPTION,
                                                  AbstractMatchEvent.EventType.CARD,
                                                  AbstractMatchEvent.EventType.FINISH_SECOND_HALF,
                                                  AbstractMatchEvent.EventType.SUBSTITUTION,
                                                  AbstractMatchEvent.EventType.FINISH_MATCH);
                break;
            case "BREAK_TIME":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.START_SECOND_HALF,
                                                  AbstractMatchEvent.EventType.STANDARD_DESCRIPTION,
                                                  AbstractMatchEvent.EventType.START_OT_SECOND_HALF,
                                                  AbstractMatchEvent.EventType.START_OT_FIRST_HALF,
                                                  AbstractMatchEvent.EventType.SUBSTITUTION);
                break;
            case "OT_FIRST_HALF":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.GOAL,
                                                  AbstractMatchEvent.EventType.STANDARD_DESCRIPTION,
                                                  AbstractMatchEvent.EventType.CARD,
                                                  AbstractMatchEvent.EventType.FINISH_OT_FIRST_HALF,
                                                  AbstractMatchEvent.EventType.SUBSTITUTION);
                break;
            case "OT_SECOND_HALF":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.GOAL,
                                                  AbstractMatchEvent.EventType.STANDARD_DESCRIPTION,
                                                  AbstractMatchEvent.EventType.CARD,
                                                  AbstractMatchEvent.EventType.FINISH_OT_SECOND_HALF,
                                                  AbstractMatchEvent.EventType.SUBSTITUTION,
                                                  AbstractMatchEvent.EventType.FINISH_MATCH);
                break;
            case "PENALTIES":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.PENALTY,
                                                  AbstractMatchEvent.EventType.FINISH_MATCH);
                break;
            case "FINISHED":
                allowedEventTypes = Arrays.asList(AbstractMatchEvent.EventType.STANDARD_DESCRIPTION);
                break;
            default:
                allowedEventTypes = Collections.emptyList();
                break;
        }
        return allowedEventTypes.contains(type);
    }

    private boolean continueEventProcessing(AbstractMatchEvent event) {
        Match match = event.getMatch();
        match.addEvent(event);

        switch (event.getType().toString()) {
            case "START_FIRST_HALF":
                match.setStatus(Match.Status.FIRST_HALF);
                break;
            case "FINISH_FIRST_HALF":
                match.setStatus(Match.Status.BREAK_TIME);
                break;
            case "START_SECOND_HALF":
                match.setStatus(Match.Status.SECOND_HALF);
                break;
            case "FINISH_SECOND_HALF":
                match.setStatus(Match.Status.BREAK_TIME);
                break;
            case "FINISH_MATCH":
                match.setStatus(Match.Status.FINISHED);
                break;
            case "START_OT_FIRST_HALF":
                match.setStatus(Match.Status.OT_FIRST_HALF);
                break;
            case "FINISH_OT_FIRST_HALF":
                match.setStatus(Match.Status.BREAK_TIME);
                break;
            case "START_OT_SECOND_HALF":
                match.setStatus(Match.Status.OT_SECOND_HALF);
                break;
            case "FINISH_OT_SECOND_HALF":
                match.setStatus(Match.Status.PENALTIES);
                break;
            case "GOAL":
                GoalEvent goalEvent = (GoalEvent)event;
                Team teamScoringGoal = goalEvent.getTeamScoring();
                if (teamScoringGoal == match.getTeamA()) {
                    match.getResult().addGoalTeamA(goalEvent.getTime(), goalEvent.getPlayerScoring());
                } else {
                    match.getResult().addGoalTeamB(goalEvent.getTime(), goalEvent.getPlayerScoring());
                }
                break;
            case "PENALTY":
                PenaltyEvent penaltyEvent = (PenaltyEvent)event;
                Team teamScoringPenalty = penaltyEvent.getTeam();
                if (teamScoringPenalty == match.getTeamA()) {
                    match.getResult().addPenaltyTeamA();
                } else {
                    match.getResult().addPenaltyTeamB();
                }
                break;
            case "STANDARD_DESCRIPTION":
            case "SUBSTITUTION":
            case "CARD":
                // events of this type do not change state
                break;
            default:
                // cases above catch all event types
                // do nothing here
                break;
        }
        return matchService.save(match) != null;
    }
}
