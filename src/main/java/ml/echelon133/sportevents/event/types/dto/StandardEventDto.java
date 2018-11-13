package ml.echelon133.sportevents.event.types.dto;

public class StandardEventDto extends MatchEventDto {

    public StandardEventDto() {}
    public StandardEventDto(Long time, String message, String type) {
        super(time, message, type);
    }
}
