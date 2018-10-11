package ml.echelon133.sportevents.stadium;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class StadiumDto {

    @NotNull
    @Length(min = 3, max = 200)
    private String name;

    @NotNull
    @Length(min = 2, max = 150)
    private String city;

    @Positive
    private Integer capacity;

    public StadiumDto() {}
    public StadiumDto(String name, String city, Integer capacity) {
        this.name = name;
        this.city = city;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public Integer getCapacity() {
        return capacity;
    }
}
