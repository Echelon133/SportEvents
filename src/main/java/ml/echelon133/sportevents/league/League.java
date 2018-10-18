package ml.echelon133.sportevents.league;

import ml.echelon133.sportevents.team.Team;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;

    @OneToMany(mappedBy = "league", cascade = CascadeType.MERGE)
    private Set<Team> teams;

    public League() {}
    public League(String name, String country) {
        this.name = name;
        this.country = country;
        this.teams = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
    }
}
