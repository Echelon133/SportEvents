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

    private Clock clock;
    private MatchRepository matchRepository;
    private TeamService teamService;
    private LeagueService leagueService;
    private StadiumService stadiumService;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository,
                            TeamService teamService,
                            LeagueService leagueService,
                            StadiumService stadiumService) {
        // Clock instance needed to simplify testing. Tests can inject Clock.fixed(...) instances to this service
        this.clock = Clock.systemDefaultZone();
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

        Match match = new Match(startDate, teamA, teamB, league, stadium);
        // Make TeamA and TeamB have a reference to this match
        match.getTeamA().addMatch(match);
        match.getTeamB().addMatch(match);

        return match;
    }

    @Override
    public Match save(Match match) {
        Match savedMatch = matchRepository.save(match);
        if (savedMatch.getWebsocketPath() == null) {
            // If websocketPath is null, it means that the entity was saved just once before (right at the top of this method)
            // This call below sets the websocketPath, because it depends on the Id value that can be accessed only
            // after the entity was saved at least once
            savedMatch.setWebsocketPath("/matches/" + savedMatch.getId());
            // Save Match entity after we set the websocketPath
            savedMatch = matchRepository.save(savedMatch);
        }
        return savedMatch;
    }

    @Override
    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    @Override
    public List<Match> findAllWithDateWithin(String within) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime future = LocalDateTime.now(clock);

        switch(within.toUpperCase()) {
            case "DAY":
                future = future.plusDays(1);
                break;
            case "THREE_DAYS":
                future = future.plusDays(3);
                break;
            case "WEEK":
                future = future.plusDays(7);
                break;
            default:
                return Collections.emptyList();
        }

        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date futureDate = Date.from(future.atZone(ZoneId.systemDefault()).toInstant());
        return matchRepository.findAllByStartDateBetween(currentDate, futureDate);
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

    public void setClock(Clock clock) {
        this.clock = clock;
    }
}
