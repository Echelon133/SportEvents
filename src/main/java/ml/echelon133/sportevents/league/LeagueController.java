package ml.echelon133.sportevents.league;

import ml.echelon133.sportevents.exception.FailedValidationException;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collections;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<LeagueResource> createLeague(@Valid @RequestBody LeagueDto leagueDto, BindingResult result)
            throws FailedValidationException{

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        League league = leagueService.convertDtoToEntity(leagueDto);
        League savedLeague = leagueService.save(league);
        LeagueResource leagueResource = resourceAssembler.toResource(savedLeague);
        return new ResponseEntity<>(leagueResource, HttpStatus.CREATED);
    }

    @PutMapping("/{leagueId}")
    public ResponseEntity<LeagueResource> replaceLeague(@PathVariable Long leagueId,
                                                       @Valid @RequestBody LeagueDto leagueDto,
                                                       BindingResult result) throws FailedValidationException,
                                                                                    ResourceDoesNotExistException {
        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        League leagueToReplace = leagueService.findById(leagueId);

        // findById did not throw ResourceDoesNotExistException, we are 100% sure that this resource can be replaced
        League replacementEntity = leagueService.convertDtoToEntity(leagueDto);
        replacementEntity.setId(leagueToReplace.getId());

        League savedLeague = leagueService.save(replacementEntity);
        LeagueResource leagueResource = resourceAssembler.toResource(savedLeague);
        return new ResponseEntity<>(leagueResource, HttpStatus.OK);
    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<Map> deleteLeague(@PathVariable Long leagueId) {
        boolean deleted = leagueService.deleteById(leagueId);
        Map<String, Boolean> response = Collections.singletonMap("deleted", deleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
