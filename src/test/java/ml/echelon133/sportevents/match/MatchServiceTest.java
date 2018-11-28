package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.league.LeagueService;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.stadium.StadiumService;
import ml.echelon133.sportevents.team.Team;
import ml.echelon133.sportevents.team.TeamService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

import static ml.echelon133.sportevents.TestUtils.buildLeague;
import static ml.echelon133.sportevents.TestUtils.buildTeam;
import static ml.echelon133.sportevents.TestUtils.getRandomMatch;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MatchServiceTest {

    private Clock fixedClock;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private LeagueService leagueService;

    @Mock
    private StadiumService stadiumService;

    @InjectMocks
    private MatchServiceImpl matchService;

    @Before
    public void before() {
        // Inject fixedClock into matchService to make generated dates easy to predict during testing
        LocalDateTime dateTime = LocalDateTime.now();
        fixedClock = Clock.fixed(dateTime.toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        matchService.setClock(fixedClock);
    }

    @Test
    public void findAllWithStatusConvertsStringToEnumValuesCorrectly() {
        Match match = getRandomMatch();
        List<Match> matches = Collections.singletonList(match);

        // Given
        given(matchRepository.findAllByStatusEquals(Match.Status.valueOf("NOT_STARTED"))).willReturn(matches);

        // When
        List<Match> results = matchService.findAllWithStatus("NOT_STARTED");

        // Then
        Match receivedMatch = results.get(0);

        assertThat(receivedMatch.getStatus()).isEqualTo(Match.Status.NOT_STARTED);
    }

    @Test
    public void findAllWithStatusReturnsEmptyListWhenStatusIncorrect() {
        // When
        List<Match> results = matchService.findAllWithStatus("NOT_EXISTING_STATUS");

        // Then
        assertThat(results).isEqualTo(Collections.emptyList());
    }

    @Test(expected = ResourceDoesNotExistException.class)
    public void findByIdThrowsExceptionIfStadiumDoesNotExist() throws Exception {
        // Given
        given(matchRepository.findById(1L)).willReturn(Optional.empty());

        // When
        matchService.findById(1L);
    }

    @Test
    public void findByIdReturnsCorrectMatchObject() throws Exception {
        Match match = getRandomMatch();

        // Given
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));

        // When
        Match receivedMatch = matchService.findById(1L);

        // Then
        assertThat(receivedMatch).isEqualTo(match);
    }

    @Test(expected = DateTimeParseException.class)
    public void convertDtoToEntityThrowsExceptionOnDateConversionFailure() throws Exception {
        MatchDto matchDto = new MatchDto("2018-10 55:99", null, null);

        // When
        Match match = matchService.convertDtoToEntity(matchDto);
    }

    @Test
    public void convertDtoToEntityNullValuesOnNonRequiredFields() throws Exception {
        // League is only needed to create valid teams, we are still passing 'null' league Id in MatchDto
        League league = new League("Test league", "Test country");
        league.setId(1L);

        Team team1 = new Team("Team1", league);
        Team team2 = new Team("Team2", league);
        team1.setId(1L);
        team2.setId(2L);

        MatchDto matchDto = new MatchDto("2020-01-01 20:00", 1L, 2L, null, null);

        // Given
        given(teamService.findById(1L)).willReturn(team1);
        given(teamService.findById(2L)).willReturn(team2);

        // When
        Match convertedMatch = matchService.convertDtoToEntity(matchDto);

        // Then
        assertThat(convertedMatch.getTeamA()).isEqualTo(team1);
        assertThat(convertedMatch.getTeamB()).isEqualTo(team2);
        assertThat(convertedMatch.getLeague()).isNull();
        assertThat(convertedMatch.getStadium()).isNull();
        assertThat(convertedMatch.getStartDate())
                .hasYear(2020).hasMonth(1).hasDayOfMonth(1).hasHourOfDay(20).hasMinute(0);
    }

    @Test
    public void convertDtoToEntityConvertsAllFieldsCorrectly() throws Exception {
        League league = new League("Test league", "Test country");
        league.setId(5L);

        Stadium stadium = new Stadium("Stadium name", "Stadium city");
        stadium.setId(10L);

        Team team1 = new Team("Team1", league);
        Team team2 = new Team("Team2", league);
        team1.setId(1L);
        team2.setId(2L);

        MatchDto matchDto = new MatchDto("2020-01-01 20:00", 1L, 2L, 5L, 10L);

        // Given
        given(teamService.findById(1L)).willReturn(team1);
        given(teamService.findById(2L)).willReturn(team2);
        given(stadiumService.findById(10L)).willReturn(stadium);
        given(leagueService.findById(5L)).willReturn(league);

        // When
        Match convertedMatch = matchService.convertDtoToEntity(matchDto);

        // Then
        assertThat(convertedMatch.getTeamA()).isEqualTo(team1);
        assertThat(convertedMatch.getTeamB()).isEqualTo(team2);
        assertThat(convertedMatch.getLeague()).isEqualTo(league);
        assertThat(convertedMatch.getStadium()).isEqualTo(stadium);
        assertThat(convertedMatch.getStartDate())
                .hasYear(2020).hasMonth(1).hasDayOfMonth(1).hasHourOfDay(20).hasMinute(0);
    }

    @Test
    public void convertDtoToEntitySetsUpMatchToTeamReferences() throws Exception {
        League league = buildLeague(1L, "Test league", "Test country");
        Team teamA = buildTeam(1L, "TeamA", league);
        Team teamB = buildTeam(2L, "TeamB", league);

        MatchDto matchDto = new MatchDto("2018-12-31 21:00", 1L, 2L, 1L, null);

        // Given
        given(teamService.findById(1L)).willReturn(teamA);
        given(teamService.findById(2L)).willReturn(teamB);
        given(leagueService.findById(1L)).willReturn(league);

        // When
        Match convertedMatch = matchService.convertDtoToEntity(matchDto);

        // Then
        List<Match> teamAMatches = convertedMatch.getTeamA().getMatches();
        List<Match> teamBMatches = convertedMatch.getTeamB().getMatches();

        assertThat(teamAMatches.contains(convertedMatch)).isTrue();
        assertThat(teamBMatches.contains(convertedMatch)).isTrue();
    }

    @Test
    public void deleteByIdReturnsCorrectResponseAfterDeletingResource() throws Exception {
        // Given
        given(matchRepository.existsById(any())).willReturn(true, false);

        // When
        boolean response = matchService.deleteById(1L);

        // Then
        assertThat(response).isTrue();
    }

    @Test
    public void findAllWithDateWithinReturnsEmptyListWhenArgumentIsInvalid() {
        // When
        List<Match> receivedMatches = matchService.findAllWithDateWithin("ASDF");

        // Then
        assertThat(receivedMatches).isEqualTo(Collections.emptyList());
    }

    @Test
    public void findAllWithDateWithinReturnsCorrectResponseForDayArgument() {
        Match match = getRandomMatch();

        List<Match> matches = Collections.singletonList(match);

        // Create two dates - one represents 'now', and the second represents a day after 'now'
        LocalDateTime dayFromNow = LocalDateTime.now(fixedClock).plusDays(1);
        Date currentDate = Date.from(fixedClock.instant());
        Date futureDate = Date.from(dayFromNow.atZone(ZoneId.systemDefault()).toInstant());

        // Given
        given(matchRepository.findAllByStartDateBetween(currentDate, futureDate)).willReturn(matches);

        // When
        List<Match> receivedMatches = matchService.findAllWithDateWithin("DAY");

        // Then
        Match receivedMatch = receivedMatches.get(0);
        assertThat(receivedMatch).isEqualTo(match);
    }


    @Test
    public void findAllWithDateWithinReturnsCorrectResponseForThreeDaysArgument() {
        Match match = getRandomMatch();

        List<Match> matches = Collections.singletonList(match);

        // Create two dates - one represents 'now', and the second represents three days after 'now'
        LocalDateTime threeDaysFromNow = LocalDateTime.now(fixedClock).plusDays(3);
        Date currentDate = Date.from(fixedClock.instant());
        Date futureDate = Date.from(threeDaysFromNow.atZone(ZoneId.systemDefault()).toInstant());

        // Given
        given(matchRepository.findAllByStartDateBetween(currentDate, futureDate)).willReturn(matches);

        // When
        List<Match> receivedMatches = matchService.findAllWithDateWithin("THREE_DAYS");

        // Then
        Match receivedMatch = receivedMatches.get(0);
        assertThat(receivedMatch).isEqualTo(match);
    }

    @Test
    public void findAllWithDateWithinReturnsCorrectResponseForWeekArgument() {
        Match match = getRandomMatch();

        List<Match> matches = Collections.singletonList(match);

        // Create two dates - one represents 'now', and the second represents a week from 'now'
        LocalDateTime weekFromNow = LocalDateTime.now(fixedClock).plusDays(7);
        Date currentDate = Date.from(fixedClock.instant());
        Date futureDate = Date.from(weekFromNow.atZone(ZoneId.systemDefault()).toInstant());

        // Given
        given(matchRepository.findAllByStartDateBetween(currentDate, futureDate)).willReturn(matches);

        // When
        List<Match> receivedMatches = matchService.findAllWithDateWithin("WEEK");

        // Then
        Match receivedMatch = receivedMatches.get(0);
        assertThat(receivedMatch).isEqualTo(match);
    }
}
