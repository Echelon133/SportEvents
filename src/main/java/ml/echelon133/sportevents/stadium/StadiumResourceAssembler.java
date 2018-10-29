package ml.echelon133.sportevents.stadium;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class StadiumResourceAssembler extends ResourceAssemblerSupport<Stadium, StadiumResource> {

    public StadiumResourceAssembler() {
        super(StadiumController.class, StadiumResource.class);
    }

    @Override
    public StadiumResource toResource(Stadium entity) {
        StadiumResource resource;
        try {
            resource = new StadiumResource(entity,
                    linkTo(StadiumController.class).withRel("stadiums"),
                    linkTo(methodOn(StadiumController.class).getStadium(entity.getId())).withSelfRel());
        } catch (ResourceDoesNotExistException ex) {
            // getStadium throws Exception only if resource (Stadium) does not exist
            // getStadium(Long id) is used here only to generate a link to a resource (methodOn creates a proxy)
            // see here: https://docs.spring.io/spring-hateoas/docs/current/api/org/springframework/hateoas/mvc/ControllerLinkBuilder.html#methodOn-java.lang.Class-java.lang.Object...-

            // this exception handling is needed only for syntactic purposes
            // see here: https://github.com/spring-projects/spring-hateoas/issues/82

            // also see: https://github.com/search?q=This+should+never+happen&type=Code&utf8=%E2%9C%93
            throw new RuntimeException("getStadium threw an exception - this should never happen");
        }
        return resource;
    }
}
