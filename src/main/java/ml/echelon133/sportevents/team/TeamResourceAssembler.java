package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.league.LeagueResource;
import ml.echelon133.sportevents.league.LeagueResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class TeamResourceAssembler extends ResourceAssemblerSupport<Team, TeamResource> {

    private LeagueResourceAssembler leagueResourceAssembler;

    @Autowired
    public TeamResourceAssembler(LeagueResourceAssembler leagueResourceAssembler) {
        super(TeamController.class, TeamResource.class);
        this.leagueResourceAssembler = leagueResourceAssembler;
    }

    @Override
    public TeamResource toResource(Team entity) {
        LeagueResource leagueResource = leagueResourceAssembler.toResource(entity.getLeague());
        TeamResource teamResource = new TeamResource(
                entity,
                leagueResource,
                linkTo(TeamController.class).withRel("teams"));
        return teamResource;
    }
}
