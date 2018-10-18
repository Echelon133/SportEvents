package ml.echelon133.sportevents.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ml.echelon133.sportevents.league.League;

import javax.persistence.*;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "league_id")
    @JsonIgnore
    private League league;

    public Team() {}
    public Team(String name, League league) {
        this.name = name;
        setLeague(league);
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

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        league.addTeam(this);
        this.league = league;
    }
}
