package ml.echelon133.sportevents.stadium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/stadiums")
public class StadiumController {

    private StadiumService stadiumService;
    private StadiumResourceAssembler resourceAssembler;

    @Autowired
    public StadiumController(StadiumService stadiumService, StadiumResourceAssembler resourceAssembler) {
        this.stadiumService = stadiumService;
        this.resourceAssembler = resourceAssembler;
    }

    @GetMapping
    public ResponseEntity<Resources<StadiumResource>> getStadiums() {
        Resources<StadiumResource> resources = new Resources<>(resourceAssembler.toResources(stadiumService.findAll()));
        resources.add(linkTo(StadiumController.class).withRel("stadiums"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
}
