package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.exception.FailedValidationException;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.match.MatchResource;
import ml.echelon133.sportevents.match.MatchResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private TeamService teamService;
    private TeamResourceAssembler teamResourceAssembler;
    private MatchResourceAssembler matchResourceAssembler;

    @Autowired
    public TeamController(TeamService teamService,
                          TeamResourceAssembler teamResourceAssembler,
                          MatchResourceAssembler matchResourceAssembler) {
        this.teamService = teamService;
        this.teamResourceAssembler = teamResourceAssembler;
        this.matchResourceAssembler = matchResourceAssembler;
    }

    @GetMapping
    public ResponseEntity<Resources<TeamResource>> getTeams(
            @RequestParam(value = "nameContains", required = false) String name) {

        List<Team> teams;
        if (name == null) {
            teams = teamService.findAll();
        } else {
            teams = teamService.findAllByNameContaining(name);
        }

        Resources<TeamResource> resources = new Resources<>(teamResourceAssembler.toResources(teams));
        resources.add(linkTo(TeamController.class).withRel("teams"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResource> getTeam(@PathVariable Long teamId) throws ResourceDoesNotExistException {
        TeamResource teamResource = teamResourceAssembler.toResource(teamService.findById(teamId));
        return new ResponseEntity<>(teamResource, HttpStatus.OK);
    }

    @GetMapping("/{teamId}/matches")
    public ResponseEntity<Resources<MatchResource>> getTeamMatches(@PathVariable Long teamId) throws ResourceDoesNotExistException {
        Team team = teamService.findById(teamId);
        Resources<MatchResource> matchResources = new Resources<>(matchResourceAssembler.toResources(team.getMatches()));
        matchResources.add(linkTo(methodOn(TeamController.class).getTeamMatches(teamId)).withRel("team-matches"));
        return new ResponseEntity<>(matchResources, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TeamResource> createTeam(@Valid @RequestBody TeamDto teamDto, BindingResult result)
            throws FailedValidationException, ResourceDoesNotExistException {

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        // Conversion fails if teamDto references a leagueId of a league that does not exist
        Team team = teamService.convertDtoToEntity(teamDto);
        // If conversion was successful, we can assume that 'team' object can be safely saved
        Team savedTeam = teamService.save(team);
        TeamResource teamResource = teamResourceAssembler.toResource(savedTeam);
        return new ResponseEntity<>(teamResource, HttpStatus.CREATED);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamResource> replaceTeam(@PathVariable Long teamId,
                                                    @Valid @RequestBody TeamDto teamDto,
                                                    BindingResult result) throws FailedValidationException,
                                                                                 ResourceDoesNotExistException {

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        Team team = teamService.findById(teamId);
        Team replacementEntity = teamService.convertDtoToEntity(teamDto);
        team = teamService.mergeChanges(team, replacementEntity);

        Team savedTeam = teamService.save(team);
        TeamResource teamResource = teamResourceAssembler.toResource(savedTeam);
        return new ResponseEntity<>(teamResource, HttpStatus.OK);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Map> deleteTeam(@PathVariable Long teamId) {
        boolean deleted = teamService.deleteById(teamId);
        Map<String, Boolean> response = Collections.singletonMap("deleted", deleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
