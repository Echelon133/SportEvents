package ml.echelon133.sportevents.stadium;

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
@RequestMapping("/api/stadiums")
public class StadiumController {

    private StadiumService stadiumService;
    private StadiumResourceAssembler resourceAssembler;

    @Autowired
    public StadiumController(StadiumService stadiumService, StadiumResourceAssembler resourceAssembler) {
        this.stadiumService = stadiumService;
        this.resourceAssembler = resourceAssembler;
    }

    @GetMapping
    public ResponseEntity<Resources<StadiumResource>> getStadiums() {
        Resources<StadiumResource> resources = new Resources<>(resourceAssembler.toResources(stadiumService.findAll()));
        resources.add(linkTo(StadiumController.class).withRel("stadiums"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @GetMapping("/{stadiumId}")
    public ResponseEntity<StadiumResource> getStadium(@PathVariable Long stadiumId) throws Exception {
        StadiumResource stadiumResource = resourceAssembler.toResource(stadiumService.findById(stadiumId));
        return new ResponseEntity<>(stadiumResource, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StadiumResource> createStadium(@Valid @RequestBody StadiumDto stadiumDto, BindingResult result)
            throws FailedValidationException {

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        Stadium stadium = stadiumService.convertDtoToEntity(stadiumDto);
        Stadium savedStadium = stadiumService.save(stadium);
        StadiumResource stadiumResource = resourceAssembler.toResource(savedStadium);
        return new ResponseEntity<>(stadiumResource, HttpStatus.CREATED);
    }

    @PutMapping("/{stadiumId}")
    public ResponseEntity<StadiumResource> replaceStadium(@PathVariable Long stadiumId,
                                                          @Valid @RequestBody StadiumDto stadiumDto,
                                                          BindingResult result) throws FailedValidationException,
            ResourceDoesNotExistException {

        if (result.hasErrors()) {
            throw new FailedValidationException(result.getFieldErrors());
        }

        Stadium stadiumToReplace = stadiumService.findById(stadiumId);

        // findById did not throw ResourceDoesNotExistException, we are 100% sure that this resource can be replaced
        Stadium replacementEntity = stadiumService.convertDtoToEntity(stadiumDto);
        replacementEntity.setId(stadiumToReplace.getId());

        Stadium savedStadium = stadiumService.save(replacementEntity);
        StadiumResource stadiumResource = resourceAssembler.toResource(savedStadium);
        return new ResponseEntity<>(stadiumResource, HttpStatus.OK);
    }

    @DeleteMapping("/{stadiumId}")
    public ResponseEntity<Map> deleteStadium(@PathVariable Long stadiumId) {
        boolean deleted = stadiumService.deleteById(stadiumId);
        Map<String, Boolean> response = Collections.singletonMap("deleted", deleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
