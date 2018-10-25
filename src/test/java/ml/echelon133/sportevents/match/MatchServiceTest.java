package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.league.LeagueService;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.stadium.StadiumService;
import ml.echelon133.sportevents.team.Team;
import ml.echelon133.sportevents.team.TeamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MatchServiceTest {

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

    @Test
    public void findAllWithStatusConvertsStringToEnumValuesCorrectly() {
        Match match = new Match(null, null, null);
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
        Match match = new Match(null, null, null);
        match.setId(1L);

        // Given
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));

        // When
        Match receivedMatch = matchService.findById(1L);

        // Then
        assertThat(receivedMatch).isEqualTo(match);
    }

    @Test(expected = ParseException.class)
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
}
