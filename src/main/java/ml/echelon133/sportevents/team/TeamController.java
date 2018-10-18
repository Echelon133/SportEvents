package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.exception.FailedValidationException;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private TeamService teamService;
    private LeagueService leagueService;
    private TeamResourceAssembler resourceAssembler;

    @Autowired
    public TeamController(TeamService teamService, LeagueService leagueService, TeamResourceAssembler resourceAssembler) {
        this.teamService = teamService;
        this.leagueService = leagueService;
        this.resourceAssembler = resourceAssembler;
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

        Resources<TeamResource> resources = new Resources<>(resourceAssembler.toResources(teams));
        resources.add(linkTo(TeamController.class).withRel("teams"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResource> getTeam(@PathVariable Long teamId) throws Exception {
        TeamResource teamResource = resourceAssembler.toResource(teamService.findById(teamId));
        return new ResponseEntity<>(teamResource, HttpStatus.OK);
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
        TeamResource teamResource = resourceAssembler.toResource(savedTeam);
        return new ResponseEntity<>(teamResource, HttpStatus.CREATED);
    }
}
