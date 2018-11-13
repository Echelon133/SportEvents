package ml.echelon133.sportevents.event.types.dto;

public class ManagingEventDto extends MatchEventDto {

    public ManagingEventDto() {}
    public ManagingEventDto(Long time, String message, String type) {
        super(time, message, type);
    }
}
