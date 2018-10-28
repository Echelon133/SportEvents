package ml.echelon133.sportevents.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private MatchResourceAssembler resourceAssembler;
    private MatchService matchService;

    @Autowired
    public MatchController(MatchResourceAssembler resourceAssembler,
                           MatchService matchService) {
        this.resourceAssembler = resourceAssembler;
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<Resources<MatchResource>> getMatches(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "dateWithin", required = false) String dateWithin) {

        List<Match> matches;
        if (status != null) {
            matches = matchService.findAllWithStatus(status);
        } else if (dateWithin != null) {
            matches = matchService.findAllWithDateWithin(dateWithin);
        } else {
            matches = matchService.findAll();
        }

        Resources<MatchResource> resources = new Resources<>(resourceAssembler.toResources(matches));
        resources.add(linkTo(MatchController.class).withRel("matches"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
}


