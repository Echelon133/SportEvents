package ml.echelon133.sportevents.league;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
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
        LeagueResource resource;
        try {
            resource = new LeagueResource(entity,
                    linkTo(LeagueController.class).withRel("leagues"),
                    linkTo(methodOn(LeagueController.class).getLeague(entity.getId())).withSelfRel());
        } catch (ResourceDoesNotExistException ex) {
            // getLeague throws Exception only if resource (League) does not exist
            // getLeague(Long id) is used here only to generate a link to a resource (methodOn creates a proxy)
            // see here: https://docs.spring.io/spring-hateoas/docs/current/api/org/springframework/hateoas/mvc/ControllerLinkBuilder.html#methodOn-java.lang.Class-java.lang.Object...-

            // this exception handling is needed only for syntactic purposes
            // see here: https://github.com/spring-projects/spring-hateoas/issues/82

            // also see: https://github.com/search?q=This+should+never+happen&type=Code&utf8=%E2%9C%93
            throw new RuntimeException("getLeague threw an exception - this should never happen");
        }
        return resource;
    }
}
