package ml.echelon133.sportevents.match;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;

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

        assert(receivedMatch.getStatus()).equals(Match.Status.NOT_STARTED);
    }

    @Test
    public void findAllWithStatusReturnsEmptyListWhenStatusIncorrect() {
        // When
        List<Match> results = matchService.findAllWithStatus("NOT_EXISTING_STATUS");

        // Then
        assert(results).isEmpty();
    }
}
