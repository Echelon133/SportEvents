package ml.echelon133.sportevents.league;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class LeagueDto {

    @Length(min = 3, max = 50)
    @NotNull
    private String name;

    @Length(min = 2, max = 30)
    @NotNull
    private String country;

    public LeagueDto() {}
    public LeagueDto(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
}
