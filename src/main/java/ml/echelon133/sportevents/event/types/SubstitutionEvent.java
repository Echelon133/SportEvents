package ml.echelon133.sportevents.event.types;

import ml.echelon133.sportevents.match.Match;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SubstitutionEvent extends AbstractMatchEvent {

    private String playerIn;

    private String playerOut;

    public SubstitutionEvent() {}
    public SubstitutionEvent(Long time, String message, EventType type, Match eventMatch, String playerIn, String playerOut) {
        super(time, message, type, eventMatch);
        this.playerIn = playerIn;
        this.playerOut = playerOut;
    }

    public String getPlayerIn() {
        return playerIn;
    }

    public void setPlayerIn(String playerIn) {
        this.playerIn = playerIn;
    }

    public String getPlayerOut() {
        return playerOut;
    }

    public void setPlayerOut(String playerOut) {
        this.playerOut = playerOut;
    }
}
