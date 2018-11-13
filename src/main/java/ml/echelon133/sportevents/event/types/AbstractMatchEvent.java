package ml.echelon133.sportevents.event.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ml.echelon133.sportevents.match.Match;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AbstractMatchEvent {

    public enum EventType {
        START_FIRST_HALF, FINISH_FIRST_HALF,
        START_SECOND_HALF, FINISH_SECOND_HALF, FINISH_MATCH,
        START_OT_FIRST_HALF, FINISH_OT_FIRST_HALF,
        START_OT_SECOND_HALF, FINISH_OT_SECOND_HALF,
        GOAL, PENALTY, STANDARD_DESCRIPTION, SUBSTITUTION, CARD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long time;

    private String message;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @ManyToOne
    @JoinColumn(name="match_id", nullable=false)
    @JsonIgnore
    private Match match;

    public AbstractMatchEvent() {}
    public AbstractMatchEvent(Long time, String message, EventType type, Match eventMatch) {
        setTime(time);
        setMessage(message);
        setType(type);
        setMatch(eventMatch);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
