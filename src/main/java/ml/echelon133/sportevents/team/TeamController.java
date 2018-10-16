package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.league.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
