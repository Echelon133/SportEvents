package ml.echelon133.sportevents.stadium;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class StadiumResource extends Resource<Stadium> {

    public StadiumResource(Stadium content, Link... links) {
        super(content, links);
    }
}
