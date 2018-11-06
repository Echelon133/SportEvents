package ml.echelon133.sportevents.event.types.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ml.echelon133.sportevents.event.types.dto.validation.ValidEventType;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public abstract class MatchEventDto {

    @NotNull
    @Range(min = 1, max = 120)
    private Long time;

    @NotNull
    private String message;

    @NotNull
    @JsonProperty(value = "type")
    @ValidEventType
    private String type;

    public MatchEventDto() {}
    public MatchEventDto(Long time, String message, String type) {
        setTime(time);
        setMessage(message);
        setType(type);
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
