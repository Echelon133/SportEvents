package ml.echelon133.sportevents.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/leagues")
public class LeagueController {

    private LeagueService leagueService;
    private LeagueResourceAssembler resourceAssembler;

    @Autowired
    public LeagueController(LeagueService leagueService, LeagueResourceAssembler resourceAssembler) {
        this.leagueService = leagueService;
        this.resourceAssembler = resourceAssembler;
    }

    @GetMapping
    public ResponseEntity<Resources<LeagueResource>> getLeagues() {
        Resources<LeagueResource> resources = new Resources<>(resourceAssembler.toResources(leagueService.findAll()));
        resources.add(linkTo(LeagueController.class).withRel("leagues"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<LeagueResource> getLeague(@PathVariable Long leagueId) throws Exception {
        LeagueResource leagueResource = resourceAssembler.toResource(leagueService.findById(leagueId));
        return new ResponseEntity<>(leagueResource, HttpStatus.OK);
    }
}
