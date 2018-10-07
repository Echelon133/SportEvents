package ml.echelon133.sportevents.league;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LeagueServiceTest {

    @Mock
    private LeagueRepository leagueRepository;

    @InjectMocks
    private LeagueServiceImpl leagueService;

    @Test(expected = IllegalArgumentException.class)
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
        League league = new League("Test league", "England");

        // When
        League convertedLeague = leagueService.convertDtoToEntity(leagueDto);

        // Then
        assertThat(convertedLeague.getId()).isNull();
        assertThat(convertedLeague.getName()).isEqualTo(league.getName());
        assertThat(convertedLeague.getCountry()).isEqualTo(league.getCountry());
    }
}
