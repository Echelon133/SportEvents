package ml.echelon133.sportevents.event.types;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StandardEvent extends AbstractMatchEvent {

    public StandardEvent() {}
    public StandardEvent(Long time, String message, EventType type) {
        setType(type);
        setTime(time);
        setMessage(message);
    }
}
