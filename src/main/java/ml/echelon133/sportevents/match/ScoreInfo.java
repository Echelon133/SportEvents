package ml.echelon133.sportevents.match;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class ScoreInfo {
    private Long teamAGoals;
    private Long teamBGoals;
    private Long teamAPenalties;
    private Long teamBPenalties;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "teamAGoalInfo_id")
    private List<GoalInfo> teamAGoalInfo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "teamBGoalInfo_id")
    private List<GoalInfo> teamBGoalInfo;

    public ScoreInfo() {
        teamAGoals = 0L;
        teamBGoals = 0L;
        teamAPenalties = 0L;
        teamBPenalties = 0L;
        teamAGoalInfo = new ArrayList<>();
        teamBGoalInfo = new ArrayList<>();
    }

    public void addGoalTeamA(Long minute, String scorerName) {
        teamAGoalInfo.add(new GoalInfo(minute, scorerName));
        teamAGoals += 1;
    }

    public void addGoalTeamB(Long minute, String scorerName) {
        teamBGoalInfo.add(new GoalInfo(minute, scorerName));
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

    public List<GoalInfo> getTeamAGoalInfo() {
        return teamAGoalInfo;
    }

    public void setTeamAGoalInfo(List<GoalInfo> teamAGoalInfo) {
        this.teamAGoalInfo = teamAGoalInfo;
    }

    public List<GoalInfo> getTeamBGoalInfo() {
        return teamBGoalInfo;
    }

    public void setTeamBGoalInfo(List<GoalInfo> teamBGoalInfo) {
        this.teamBGoalInfo = teamBGoalInfo;
    }
}
