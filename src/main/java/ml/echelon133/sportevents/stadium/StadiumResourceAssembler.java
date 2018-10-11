package ml.echelon133.sportevents.stadium;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class StadiumResourceAssembler extends ResourceAssemblerSupport<Stadium, StadiumResource> {

    public StadiumResourceAssembler() {
        super(StadiumController.class, StadiumResource.class);
    }

    @Override
    public StadiumResource toResource(Stadium entity) {
        return new StadiumResource(entity, linkTo(StadiumController.class).withRel("stadiums"));
    }
}
