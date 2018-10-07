package ml.echelon133.sportevents.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
