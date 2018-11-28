package ml.echelon133.sportevents.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.match.Match;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "team_matches",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "match_id", referencedColumnName = "id")
    )
    @JsonIgnore
    private List<Match> matches;

    public Team() {}
    public Team(String name, League league) {
        this.name = name;
        this.matches = new ArrayList<>();
        this.league = league;
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
        this.league = league;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public void removeMatch(Match match) {
        long matchId = match.getId();
        matches.removeIf(m -> m.getId() == matchId);
    }
}
