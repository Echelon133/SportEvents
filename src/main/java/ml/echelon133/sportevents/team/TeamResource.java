package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.league.LeagueResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class TeamResource extends Resource<Team> {

    LeagueResource league;

    public TeamResource(Team content, Link... links) {
        super(content, links);
    }

    public TeamResource(Team content, LeagueResource league, Link... links) {
        super(content, links);
        this.league = league;
    }

    public LeagueResource getLeague() {
        return league;
    }
}
