package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.league.LeagueService;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.stadium.StadiumService;
import ml.echelon133.sportevents.team.Team;
import ml.echelon133.sportevents.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class MatchServiceImpl implements MatchService {

    private MatchRepository matchRepository;
    private TeamService teamService;
    private LeagueService leagueService;
    private StadiumService stadiumService;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository,
                            TeamService teamService,
                            LeagueService leagueService,
                            StadiumService stadiumService) {
        this.matchRepository = matchRepository;
        this.teamService = teamService;
        this.leagueService = leagueService;
        this.stadiumService = stadiumService;
    }

    private Date convertStringToDate(String dateAsString) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime datetime = LocalDateTime.parse(dateAsString, formatter);
        return Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Match convertDtoToEntity(MatchDto matchDto) throws ResourceDoesNotExistException, DateTimeParseException {
        Date startDate;
        Team teamA;
        Team teamB;
        League league;
        Stadium stadium;

        // startDate, teamA, teamB must not be null
        startDate = convertStringToDate(matchDto.getStartDate());
        teamA = teamService.findById(matchDto.getTeamA());
        teamB = teamService.findById(matchDto.getTeamB());

        // league/stadium can be set to null if they are not given by the user (they are not required)
        if (matchDto.getLeague() == null) {
            league = null;
        } else {
            league = leagueService.findById(matchDto.getLeague());
        }

        if (matchDto.getStadium() == null) {
            stadium = null;
        } else {
            stadium = stadiumService.findById(matchDto.getStadium());
        }

        return new Match(startDate, teamA, teamB, league, stadium);
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
        boolean exists = matchRepository.existsById(id);
        if (exists) {
            matchRepository.deleteById(id);
            exists = matchRepository.existsById(id);
        }
        return !exists;
    }
}
