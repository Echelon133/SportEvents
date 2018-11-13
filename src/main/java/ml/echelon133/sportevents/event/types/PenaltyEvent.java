package ml.echelon133.sportevents.event.types;

import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.team.Team;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PenaltyEvent extends AbstractMatchEvent {

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name="teamScored_id")
    private Team team;

    public PenaltyEvent() {}
    public PenaltyEvent(Long time, String message, EventType type, Match eventMatch, Team team) {
        super(time, message, type, eventMatch);
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
