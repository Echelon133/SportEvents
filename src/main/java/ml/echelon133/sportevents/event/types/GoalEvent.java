package ml.echelon133.sportevents.event.types;

import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.team.Team;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class GoalEvent extends AbstractMatchEvent {

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name="team_id")
    private Team teamScoring;

    private String playerScoring;

    public GoalEvent() {}
    public GoalEvent(Long time, String message, EventType type, Match eventMatch, Team teamScoring, String playerScoring) {
        super(time, message, type, eventMatch);
        this.teamScoring = teamScoring;
        this.playerScoring = playerScoring;
    }

    public Team getTeamScoring() {
        return teamScoring;
    }

    public void setTeamScoring(Team teamScoring) {
        this.teamScoring = teamScoring;
    }

    public String getPlayerScoring() {
        return playerScoring;
    }

    public void setPlayerScoring(String playerScoring) {
        this.playerScoring = playerScoring;
    }
}
