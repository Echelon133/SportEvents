package ml.echelon133.sportevents.event.types;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ManagingEvent extends AbstractMatchEvent {

    public ManagingEvent() {}
    public ManagingEvent(Long time, String message, EventType type) {
        setTime(time);
        setMessage(message);
        setType(type);
    }
}
