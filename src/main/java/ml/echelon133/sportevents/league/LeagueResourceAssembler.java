package ml.echelon133.sportevents.league;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class LeagueResourceAssembler extends ResourceAssemblerSupport<League, LeagueResource> {

    public LeagueResourceAssembler() {
        super(LeagueController.class, LeagueResource.class);
    }

    @Override
    public LeagueResource toResource(League entity) {
        return new LeagueResource(entity,
                                  linkTo(LeagueController.class).withRel("leagues"),
                                  linkTo(methodOn(LeagueController.class).getLeague(entity.getId())).withSelfRel());
    }
}
