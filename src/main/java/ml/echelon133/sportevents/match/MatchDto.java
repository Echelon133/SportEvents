package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.match.validation.TeamIdsNotEqual;

import javax.validation.constraints.NotNull;

@TeamIdsNotEqual
public class MatchDto {

    @NotNull
    private Long teamA;

    @NotNull
    private Long teamB;

    private Long league;

    private Long stadium;

    @NotNull
    private String startDate;

    public MatchDto() {}
    public MatchDto(String startDate, Long teamA, Long teamB) {
        this.startDate = startDate;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public MatchDto(String startDate, Long teamA, Long teamB, Long league) {
        this(startDate, teamA, teamB);
        this.league = league;
    }

    public MatchDto(String startDate, Long teamA, Long teamB, Long league, Long stadium) {
        this(startDate, teamA, teamB, league);
        this.stadium = stadium;
    }

    public Long getTeamA() {
        return teamA;
    }

    public void setTeamA(Long teamA) {
        this.teamA = teamA;
    }

    public Long getTeamB() {
        return teamB;
    }

    public void setTeamB(Long teamB) {
        this.teamB = teamB;
    }

    public Long getLeague() {
        return league;
    }

    public void setLeague(Long league) {
        this.league = league;
    }

    public Long getStadium() {
        return stadium;
    }

    public void setStadium(Long stadium) {
        this.stadium = stadium;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
