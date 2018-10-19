package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.league.LeagueService;
import ml.echelon133.sportevents.stadium.StadiumService;
import ml.echelon133.sportevents.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private LeagueService leagueService;
    private StadiumService stadiumService;
    private TeamService teamService;

    @Autowired
    public MatchController(LeagueService leagueService,
                           StadiumService stadiumService,
                           TeamService teamService) {
        this.leagueService = leagueService;
        this.stadiumService = stadiumService;
        this.teamService = teamService;
    }
}


