package ml.echelon133.sportevents.event.types.dto;

import javax.validation.constraints.NotNull;

public class SubstitutionEventDto extends MatchEventDto {

    @NotNull
    private String playerIn;

    @NotNull
    private String playerOut;

    public SubstitutionEventDto() {}
    public SubstitutionEventDto(Long time, String message, String type, String playerIn, String playerOut) {
        super(time, message, type);
        this.playerIn = playerIn;
        this.playerOut = playerOut;
    }

    public String getPlayerIn() {
        return playerIn;
    }

    public String getPlayerOut() {
        return playerOut;
    }
}
