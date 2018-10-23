package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;

import java.util.List;

public interface MatchService {

    enum DateWithin {
        DAY, THREE_DAYS, WEEK, MONTH
    }

    Match convertDtoToEntity(MatchDto matchDto) throws ResourceDoesNotExistException;
    Match save(Match match);
    List<Match> findAll();
    List<Match> findAllWithDateWithin(DateWithin time);
    List<Match> findAllWithStatus(Match.Status status);
    Match findById(Long id) throws ResourceDoesNotExistException;
    boolean deleteById(Long id);
}
