package ml.echelon133.sportevents.league;

import ml.echelon133.sportevents.exception.FailedValidationException;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.team.TeamController;
import ml.echelon133.sportevents.team.TeamResource;
import ml.echelon133.sportevents.team.TeamResourceAssembler;
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
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/leagues")
public class LeagueController {

    private LeagueService leagueService;
    private LeagueResourceAssembler leagueResourceAssembler;
    private TeamResourceAssembler teamResourceAssembler;

    @Autowired
    public LeagueController(LeagueService leagueService,
                            LeagueResourceAssembler leagueResourceAssembler,
                            TeamResourceAssembler teamResourceAssembler) {
        this.leagueService = leagueService;
        this.leagueResourceAssembler = leagueResourceAssembler;
        this.teamResourceAssembler = teamResourceAssembler;
    }

    @GetMapping
    public ResponseEntity<Resources<LeagueResource>> getLeagues() {
        Resources<LeagueResource> resources = new Resources<>(leagueResourceAssembler.toResources(leagueService.findAll()));
        resources.add(linkTo(LeagueController.class).withRel("leagues"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<LeagueResource> getLeague(@PathVariable Long leagueId) throws ResourceDoesNotExistException {
        LeagueResource leagueResource = leagueResourceAssembler.toResource(leagueService.findById(leagueId));
        return new ResponseEntity<>(leagueResource, HttpStatus.OK);
    }

    @GetMapping("/{leagueId}/teams")
    public ResponseEntity<Resources<TeamResource>> getLeagueTeams(@PathVariable Long leagueId) throws ResourceDoesNotExistException {
        League league = leagueService.findById(leagueId);
        Resources<TeamResource> resources = new Resources<>(teamResourceAssembler.toResources(league.getTeams()));
        resources.add(linkTo(methodOn(LeagueController.class).getLeagueTeams(leagueId)).withRel("league-teams"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LeagueResource> createLeague(@Valid @RequestBody LeagueDto leagueDto, BindingResult result)
            throws FailedValidationException{

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        League league = leagueService.convertDtoToEntity(leagueDto);
        League savedLeague = leagueService.save(league);
        LeagueResource leagueResource = leagueResourceAssembler.toResource(savedLeague);
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
        LeagueResource leagueResource = leagueResourceAssembler.toResource(savedLeague);
        return new ResponseEntity<>(leagueResource, HttpStatus.OK);
    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<Map> deleteLeague(@PathVariable Long leagueId) {
        boolean deleted = leagueService.deleteById(leagueId);
        Map<String, Boolean> response = Collections.singletonMap("deleted", deleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
