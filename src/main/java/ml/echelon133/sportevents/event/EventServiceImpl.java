package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

    private MatchService matchService;

    @Autowired
    public EventServiceImpl(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public boolean processEvent(AbstractMatchEvent event, Match match) throws ProcessedEventRejectedException {
        boolean processedAndSaved = false;

        if (isValidEventTypeForMatchStatus(match.getStatus(), event.getType())) {
            processedAndSaved = continueEventProcessing(event, match);
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

    private boolean continueEventProcessing(AbstractMatchEvent event, Match match) {
        return false;
    }
}
