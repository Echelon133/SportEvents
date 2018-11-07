package ml.echelon133.sportevents.event.types;

import ml.echelon133.sportevents.match.Match;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ManagingEvent extends AbstractMatchEvent {

    public ManagingEvent() {}
    public ManagingEvent(Long time, String message, EventType type, Match eventMatch) {
        super(time, message, type, eventMatch);
    }
}
