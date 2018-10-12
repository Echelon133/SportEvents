package ml.echelon133.sportevents.stadium;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;

import java.util.List;

public interface StadiumService {
    Stadium convertDtoToEntity(StadiumDto leagueDto);
    Stadium save(Stadium league);
    List<Stadium> findAll();
    Stadium findById(Long id) throws ResourceDoesNotExistException;
    boolean deleteById(Long id);
}
