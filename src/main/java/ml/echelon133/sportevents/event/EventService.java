package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.match.Match;

public interface EventService {
    boolean processEvent(AbstractMatchEvent event, Match match) throws StatusChangeAttemptFailedException;
}
