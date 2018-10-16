package ml.echelon133.sportevents.team;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class TeamDto {

    @NotNull
    @Length(min = 2, max = 200)
    private String name;

    @NotNull
    private Long leagueId;

    public TeamDto() {}
    public TeamDto(String name, Long leagueId) {
        this.name = name;
        this.leagueId = leagueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }
}
