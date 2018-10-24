package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
        return null;
    }

    @Override
    public List<Match> findAll() {
        return null;
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
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
