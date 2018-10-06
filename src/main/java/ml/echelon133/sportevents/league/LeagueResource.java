package ml.echelon133.sportevents.league;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class LeagueResource extends Resource<League> {

    public LeagueResource(League content, Link... links) {
        super(content, links);
    }
}
