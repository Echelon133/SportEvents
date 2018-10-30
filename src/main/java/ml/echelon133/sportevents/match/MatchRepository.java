package ml.echelon133.sportevents.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByStatusEquals(Match.Status status);
    List<Match> findAllByStartDateBetween(Date date1, Date date2);
}
