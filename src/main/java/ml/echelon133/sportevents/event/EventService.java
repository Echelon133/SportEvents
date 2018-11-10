package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;

public interface EventService {
    boolean processEvent(AbstractMatchEvent event) throws ProcessedEventRejectedException;
}
