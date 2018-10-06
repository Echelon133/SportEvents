package ml.echelon133.sportevents.league;

import java.util.List;

public interface LeagueService {
    League convertDtoToEntity(LeagueDto leagueDto);
    League save(League league);
    List<League> findAll();
    League findById(Long id) throws IllegalArgumentException;
}
