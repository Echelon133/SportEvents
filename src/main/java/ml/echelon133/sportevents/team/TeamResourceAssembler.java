package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.LeagueResource;
import ml.echelon133.sportevents.league.LeagueResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
        TeamResource resource;
        try {
            resource = new TeamResource(entity,
                    leagueResource,
                    linkTo(TeamController.class).withRel("teams"),
                    linkTo(methodOn(TeamController.class).getTeam(entity.getId())).withSelfRel(),
                    linkTo(methodOn(TeamController.class).getTeamMatches(entity.getId())).withRel("team-matches"));
        } catch (ResourceDoesNotExistException ex) {
            // getTeam throws Exception only if resource (Team) does not exist
            // getTeam(Long id) is used here only to generate a link to a resource (methodOn creates a proxy)
            // see here: https://docs.spring.io/spring-hateoas/docs/current/api/org/springframework/hateoas/mvc/ControllerLinkBuilder.html#methodOn-java.lang.Class-java.lang.Object...-

            // this exception handling is needed only for syntactic purposes
            // see here: https://github.com/spring-projects/spring-hateoas/issues/82

            // also see: https://github.com/search?q=This+should+never+happen&type=Code&utf8=%E2%9C%93
            throw new RuntimeException("getTeam threw an exception - this should never happen");
        }
        return resource;
    }
}
