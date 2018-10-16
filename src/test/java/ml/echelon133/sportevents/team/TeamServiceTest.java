package ml.echelon133.sportevents.team;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.league.LeagueService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private LeagueService leagueService;

    @InjectMocks
    private TeamServiceImpl teamService;

    @Test(expected = ResourceDoesNotExistException.class)
    public void findByIdThrowsExceptionIfTeamDoesNotExist() throws Exception {
        // Given
        given(teamRepository.findById(1L)).willReturn(Optional.empty());

        // When
        teamService.findById(1L);
    }

    @Test(expected = ResourceDoesNotExistException.class)
    public void convertDtoToEntityThrowsExceptionIfLeagueDoesNotExist() throws Exception {
        TeamDto teamDto = new TeamDto("Team name", 1L);

        // Given
        given(leagueService.findById(1L)).willThrow(new ResourceDoesNotExistException("League with this id does not exist"));

        // When
        teamService.convertDtoToEntity(teamDto);
    }

    @Test
    public void convertDtoToEntityConvertsCorrectly() throws Exception {
        TeamDto teamDto = new TeamDto("Team name", 1L);

        League league = new League("Test league", "Test country");
        league.setId(1L);

        // Given
        given(leagueService.findById(1L)).willReturn(league);

        // When
        Team team = teamService.convertDtoToEntity(teamDto);

        // Then
        assertThat(team.getId()).isNull();
        assertThat(team.getName()).isEqualTo(teamDto.getName());
        assertThat(team.getLeague().getId()).isEqualTo(league.getId());
        assertThat(team.getLeague().getName()).isEqualTo(league.getName());
        assertThat(team.getLeague().getCountry()).isEqualTo(league.getCountry());
    }

    @Test
    public void deleteByIdReturnsCorrectResponseWhenResourceAlreadyDeleted() throws Exception {
        // Given
        given(teamRepository.existsById(any())).willReturn(false);

        // When
        boolean response = teamService.deleteById(1L);

        // Then
        assertThat(response).isTrue();
    }

    @Test
    public void deleteByIdReturnsCorrectResponseAfterDeletingResource() throws Exception {
        // Given
        given(teamRepository.existsById(any())).willReturn(true, false);

        // When
        boolean response = teamService.deleteById(1L);

        // Then
        assertThat(response).isTrue();
    }
}
