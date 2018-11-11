package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.*;
import ml.echelon133.sportevents.event.types.dto.*;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import ml.echelon133.sportevents.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private MatchService matchService;

    @Autowired
    public EventServiceImpl(MatchService matchService) {
        this.matchService = matchService;
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

        if (isValidEventTypeForMatchStatus(match.getStatus(), event.getType())) {
            processedAndSaved = continueEventProcessing(event);
        } else {
            String message = String
                    .format("Cannot send events of type %s when match status is %s%n", event.getType().toString(),
                                                                                       match.getStatus().toString());
            throw new ProcessedEventRejectedException(message);
        }
        return processedAndSaved;
    }

    private boolean isValidEventTypeForMatchStatus(Match.Status status, AbstractMatchEvent.EventType type) {
        return false;
    }

    private boolean continueEventProcessing(AbstractMatchEvent event) {
        return false;
    }
}
