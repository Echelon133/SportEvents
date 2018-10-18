package ml.echelon133.sportevents.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByNameContaining(String text);
}
