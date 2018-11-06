package ml.echelon133.sportevents.event.types.dto;

public class PenaltyEventDto extends MatchEventDto {

    private Long teamId;

    public PenaltyEventDto() {}
    public PenaltyEventDto(Long time, String message, String type, Long teamId) {
        super(time, message, type);
        this.teamId = teamId;
    }

    public Long getTeamId() {
        return teamId;
    }
}
