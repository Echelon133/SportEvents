package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MatchServiceImpl implements MatchService {

    private MatchRepository matchRepository;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Match convertDtoToEntity(MatchDto matchDto) throws ResourceDoesNotExistException {
        return null;
    }

    @Override
    public Match save(Match match) {
        return matchRepository.save(match);
    }

    @Override
    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    @Override
    public List<Match> findAllWithDateWithin(String within) {
        return null;
    }

    @Override
    public List<Match> findAllWithStatus(String statusAsText) {
        Match.Status status;
        List<Match> matches;
        try {
            status = Match.Status.valueOf(statusAsText);
            matches = matchRepository.findAllByStatusEquals(status);
        } catch (IllegalArgumentException ex) {
            matches = Collections.emptyList();
        }
        return matches;
    }

    @Override
    public Match findById(Long id) throws ResourceDoesNotExistException {
        Optional<Match> match = matchRepository.findById(id);
        if (match.isPresent()) {
            return match.get();
        }
        throw new ResourceDoesNotExistException("Match with this id does not exist");
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
