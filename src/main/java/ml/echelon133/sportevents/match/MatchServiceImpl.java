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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private Date convertStringToDate(String dateAsString) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        return format.parse(dateAsString);
    }

    @Override
    public Match convertDtoToEntity(MatchDto matchDto) throws ResourceDoesNotExistException, ParseException {
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
        return false;
    }
}
