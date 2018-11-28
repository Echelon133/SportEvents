package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.event.EventController;
import ml.echelon133.sportevents.exception.FailedValidationException;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.LeagueController;
import ml.echelon133.sportevents.stadium.StadiumController;
import ml.echelon133.sportevents.team.TeamController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        resources.add(linkTo(LeagueController.class).withRel("leagues"));
        resources.add(linkTo(TeamController.class).withRel("teams"));
        resources.add(linkTo(StadiumController.class).withRel("stadiums"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResource> getMatch(@PathVariable Long matchId) throws ResourceDoesNotExistException {
        MatchResource matchResource = resourceAssembler.toResource(matchService.findById(matchId));
        return new ResponseEntity<>(matchResource, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MatchResource> createMatch(@Valid @RequestBody MatchDto matchDto, BindingResult result)
            throws ResourceDoesNotExistException, FailedValidationException, DateTimeParseException {

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors(), result.getGlobalErrors());
        }

        Match match = matchService.convertDtoToEntity(matchDto);
        Match savedMatch = matchService.save(match);
        MatchResource resource = resourceAssembler.toResource(savedMatch);
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<MatchResource> replaceMatch(@PathVariable Long matchId,
                                                      @Valid @RequestBody MatchDto matchDto,
                                                      BindingResult result)
            throws FailedValidationException, ResourceDoesNotExistException, DateTimeParseException {

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors(), result.getGlobalErrors());
        }

        Match match = matchService.findById(matchId);
        Match replacementEntity = matchService.convertDtoToEntity(matchDto);

        // Merge all changes into match
        match = matchService.mergeChanges(match, replacementEntity);

        Match savedMatch = matchService.save(match);
        MatchResource matchResource = resourceAssembler.toResource(savedMatch);
        return new ResponseEntity<>(matchResource, HttpStatus.OK);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Map> deleteMatch(@PathVariable Long matchId) {
        boolean deleted = matchService.deleteById(matchId);
        Map<String, Boolean> response = Collections.singletonMap("deleted", deleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


