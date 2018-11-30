package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;

import java.time.format.DateTimeParseException;
import java.util.List;

public interface MatchService {
    Match convertDtoToEntity(MatchDto matchDto) throws ResourceDoesNotExistException, DateTimeParseException;
    Match mergeChanges(Match originalMatch, Match replacementMatch);
    Match save(Match match);
    List<Match> findAll();
    List<Match> findAllWithDateWithin(String dateWithin);
    List<Match> findAllWithStatus(String status);
    Match findById(Long id) throws ResourceDoesNotExistException;
    boolean deleteById(Long id);
}
