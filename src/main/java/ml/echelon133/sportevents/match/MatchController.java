package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.FailedValidationException;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeParseException;
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

        Match matchToReplace = matchService.findById(matchId);

        Match replacementEntity = matchService.convertDtoToEntity(matchDto);
        replacementEntity.setId(matchToReplace.getId());

        Match savedMatch = matchService.save(replacementEntity);
        MatchResource matchResource = resourceAssembler.toResource(savedMatch);
        return new ResponseEntity<>(matchResource, HttpStatus.OK);
    }
}


