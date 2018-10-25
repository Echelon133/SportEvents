package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

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
}
