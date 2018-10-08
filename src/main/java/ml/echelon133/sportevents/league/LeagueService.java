package ml.echelon133.sportevents.league;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;

import java.util.List;

public interface LeagueService {
    League convertDtoToEntity(LeagueDto leagueDto);
    League save(League league);
    List<League> findAll();
    League findById(Long id) throws ResourceDoesNotExistException;
}
