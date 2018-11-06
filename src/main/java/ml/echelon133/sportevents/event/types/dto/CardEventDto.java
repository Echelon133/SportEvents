package ml.echelon133.sportevents.event.types.dto;

import ml.echelon133.sportevents.event.types.dto.validation.ValidCardColor;

import javax.validation.constraints.NotNull;

public class CardEventDto extends MatchEventDto {

    @NotNull
    private String player;

    @NotNull
    @ValidCardColor
    private String color;

    public CardEventDto() {}
    public CardEventDto(Long time, String message, String type, String player, String color) {
        super(time, message, type);
        this.player = player;
        this.color = color;
    }

    public String getPlayer() {
        return player;
    }

    public String getColor() {
        return color;
    }
}
