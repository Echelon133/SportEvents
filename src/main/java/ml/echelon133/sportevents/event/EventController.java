package ml.echelon133.sportevents.event;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.event.types.dto.MatchEventDto;
import ml.echelon133.sportevents.exception.FailedValidationException;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches/{matchId}")
public class EventController {

    private MatchService matchService;
    private EventService eventService;

    @Autowired
    public EventController(MatchService matchService, EventService eventService) {
        this.matchService = matchService;
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<AbstractMatchEvent>> getEvents(@PathVariable Long matchId) throws ResourceDoesNotExistException {
        Match match = matchService.findById(matchId);
        return new ResponseEntity<>(match.getEvents(), HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<Map<String, Boolean>> receiveEvent(@PathVariable Long matchId, @Valid @RequestBody MatchEventDto eventDto, BindingResult result)
            throws FailedValidationException, ResourceDoesNotExistException, ProcessedEventRejectedException {

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        Match match = matchService.findById(matchId);
        AbstractMatchEvent event = eventService.convertEventDtoToEntity(eventDto, match);
        boolean processingResult = eventService.processEvent(event);
        return new ResponseEntity<>(Collections.singletonMap("eventProcessed", processingResult), HttpStatus.OK);
    }
}
