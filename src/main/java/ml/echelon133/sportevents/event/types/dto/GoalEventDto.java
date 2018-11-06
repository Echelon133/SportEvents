package ml.echelon133.sportevents.event.types.dto;

import javax.validation.constraints.NotNull;

public class GoalEventDto extends MatchEventDto {

    @NotNull
    private Long teamId;

    @NotNull
    private String scorerName;

    public GoalEventDto() {}
    public GoalEventDto(Long time, String message, String type, Long teamId, String scorerName) {
        super(time, message, type);
        this.teamId = teamId;
        this.scorerName = scorerName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getScorerName() {
        return scorerName;
    }

    public void setScorerName(String scorerName) {
        this.scorerName = scorerName;
    }
}
