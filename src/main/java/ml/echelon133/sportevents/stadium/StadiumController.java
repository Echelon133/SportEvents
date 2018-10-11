package ml.echelon133.sportevents.stadium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/stadiums")
public class StadiumController {

    private StadiumService stadiumService;
    private StadiumResourceAssembler resourceAssembler;

    @Autowired
    public StadiumController(StadiumService stadiumService, StadiumResourceAssembler resourceAssembler) {
        this.stadiumService = stadiumService;
        this.resourceAssembler = resourceAssembler;
    }
}
