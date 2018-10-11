package ml.echelon133.sportevents.league;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeagueServiceImpl implements LeagueService {

    private LeagueRepository leagueRepository;

    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @Override
    public League convertDtoToEntity(LeagueDto leagueDto) {
        return new League(leagueDto.getName(), leagueDto.getCountry());
    }

    @Override
    public League save(League league) {
        return leagueRepository.save(league);
    }

    @Override
    public List<League> findAll() {
        return leagueRepository.findAll();
    }

    @Override
    public League findById(Long id) throws ResourceDoesNotExistException {
        Optional<League> league = leagueRepository.findById(id);
        if (league.isPresent()) {
            return league.get();
        }
        throw new ResourceDoesNotExistException("League with this id does not exist");
    }

    @Override
    public boolean deleteById(Long id) {
        boolean exists = leagueRepository.existsById(id);
        if (exists) {
            leagueRepository.deleteById(id);
            exists = leagueRepository.existsById(id);
        }
        return !exists;
    }
}
