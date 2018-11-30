package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.league.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    private TeamRepository teamRepository;
    private LeagueService leagueService;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, LeagueService leagueService) {
        this.teamRepository = teamRepository;
        this.leagueService = leagueService;
    }

    @Override
    public Team mergeChanges(Team team, Team replacementTeam) {
        // If current team and replacementTeam belong to different leagues, fix league-team relationship
        // Remove old team reference from a league, change team league and add a new team reference to the new league
        League teamLeague = team.getLeague();
        League replacementLeague = replacementTeam.getLeague();

        if (teamLeague != replacementLeague) {
            // A Team with league set to null cannot be created, but to avoid any unexpected NullPointerException
            // we can still try to handle this safely
            if (teamLeague != null) {
                teamLeague.removeTeam(team);
            }
            if (replacementLeague != null) {
                replacementLeague.addTeam(team);
            }
            team.setLeague(replacementLeague);
        }

        // Set remaining field
        team.setName(replacementTeam.getName());
        return team;
    }

    @Override
    public Team convertDtoToEntity(TeamDto teamDto) throws ResourceDoesNotExistException{
        Team team = new Team();

        // This method lets leagueService throw ResourceDoesNotExistException because there is no reason
        // to convert DTO to an entity that has league set to null.
        // All code after the conversion assumes that entity conversion was fully successful
        League league = leagueService.findById(teamDto.getLeagueId());

        team.setName(teamDto.getName());
        league.addTeam(team);
        team.setLeague(league);
        return team;
    }

    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Override
    public List<Team> findAllByNameContaining(String name) {
        return teamRepository.findAllByNameContaining(name);
    }

    @Override
    public Team findById(Long id) throws ResourceDoesNotExistException {
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()) {
            return team.get();
        }
        throw new ResourceDoesNotExistException("Team with this id does not exist");
    }

    @Override
    public boolean deleteById(Long id) {
        boolean exists = teamRepository.existsById(id);
        if (exists) {
            teamRepository.deleteById(id);
            exists = teamRepository.existsById(id);
        }
        return !exists;
    }
}
