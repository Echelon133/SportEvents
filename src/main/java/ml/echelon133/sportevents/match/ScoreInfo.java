package ml.echelon133.sportevents.match;

import javax.persistence.Embeddable;

@Embeddable
public class ScoreInfo {
    private Long teamAGoals;
    private Long teamBGoals;
    private Long teamAPenalties;
    private Long teamBPenalties;

    public ScoreInfo() {
        teamAGoals = 0L;
        teamBGoals = 0L;
        teamAPenalties = 0L;
        teamBPenalties = 0L;
    }

    public void addGoalTeamA() {
        teamAGoals += 1;
    }

    public void addGoalTeamB() {
        teamBGoals += 1;
    }

    public void addPenaltyTeamA() {
        teamAPenalties += 1;
    }

    public void addPenaltyTeamB() {
        teamBPenalties += 1;
    }

    public Long getTeamAGoals() {
        return teamAGoals;
    }

    public void setTeamAGoals(Long teamAGoals) {
        this.teamAGoals = teamAGoals;
    }

    public Long getTeamBGoals() {
        return teamBGoals;
    }

    public void setTeamBGoals(Long teamBGoals) {
        this.teamBGoals = teamBGoals;
    }

    public Long getTeamAPenalties() {
        return teamAPenalties;
    }

    public void setTeamAPenalties(Long teamAPenalties) {
        this.teamAPenalties = teamAPenalties;
    }

    public Long getTeamBPenalties() {
        return teamBPenalties;
    }

    public void setTeamBPenalties(Long teamBPenalties) {
        this.teamBPenalties = teamBPenalties;
    }
}
