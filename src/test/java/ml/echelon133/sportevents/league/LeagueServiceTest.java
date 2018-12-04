package ml.echelon133.sportevents.league;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static ml.echelon133.sportevents.TestUtils.buildLeague;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LeagueServiceTest {

    @Mock
    private LeagueRepository leagueRepository;

    @InjectMocks
    private LeagueServiceImpl leagueService;

    @Test(expected = ResourceDoesNotExistException.class)
    public void findByIdThrowsExceptionIfLeagueDoesNotExist() throws Exception {
        // Given
        given(leagueRepository.findById(1L)).willReturn(Optional.empty());

        // When
        leagueService.findById(1L);
    }

    @Test
    public void convertDtoToEntityConvertsCorrectly() throws Exception {
        LeagueDto leagueDto = new LeagueDto("Test league", "England");

        // Expected object
        League league = buildLeague(null, "Test league", "England");

        // When
        League convertedLeague = leagueService.convertDtoToEntity(leagueDto);

        // Then
        assertThat(convertedLeague.getId()).isNull();
        assertThat(convertedLeague.getName()).isEqualTo(league.getName());
        assertThat(convertedLeague.getCountry()).isEqualTo(league.getCountry());
    }

    @Test
    public void deleteByIdReturnsCorrectResponseWhenResourceAlreadyDeleted() throws Exception {
        // Given
        given(leagueRepository.existsById(any())).willReturn(false);

        // When
        boolean response = leagueService.deleteById(1L);

        // Then
        assertThat(response).isTrue();
    }

    @Test
    public void deleteByIdReturnsCorrectResponseAfterDeletingResource() throws Exception {
        // Given
        given(leagueRepository.existsById(any())).willReturn(true, false);

        // When
        boolean response = leagueService.deleteById(1L);

        // Then
        assertThat(response).isTrue();
    }
}
