package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.event.types.GoalEvent;
import ml.echelon133.sportevents.event.types.StandardEvent;
import ml.echelon133.sportevents.event.types.SubstitutionEvent;
import ml.echelon133.sportevents.event.types.dto.MatchEventDto;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import ml.echelon133.sportevents.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/matches/{matchId}")
public class EventController {

    private MatchService matchService;

    @Autowired
    public EventController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<AbstractMatchEvent>> getEvents(@PathVariable Long matchId) throws Exception {
        Match match = matchService.findById(matchId);
        return new ResponseEntity<>(match.getEvents(), HttpStatus.OK);
    }
}
