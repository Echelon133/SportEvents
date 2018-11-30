package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;

import java.util.List;

public interface TeamService {
    Team convertDtoToEntity(TeamDto teamDto) throws ResourceDoesNotExistException;
    Team mergeChanges(Team team, Team replacementTeam);
    Team save(Team team);
    List<Team> findAll();
    List<Team> findAllByNameContaining(String name);
    Team findById(Long id) throws ResourceDoesNotExistException;
    boolean deleteById(Long id);
}
