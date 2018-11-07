package ml.echelon133.sportevents.event.types;

import ml.echelon133.sportevents.match.Match;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StandardEvent extends AbstractMatchEvent {

    public StandardEvent() {}
    public StandardEvent(Long time, String message, EventType type, Match matchEvent) {
        super(time, message, type, matchEvent);
    }
}
