package ml.echelon133.sportevents.stadium;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
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
public class StadiumServiceTest {

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private StadiumServiceImpl stadiumService;

    @Test(expected = ResourceDoesNotExistException.class)
    public void findByIdThrowsExceptionIfStadiumDoesNotExist() throws Exception {
        // Given
        given(stadiumRepository.findById(1L)).willReturn(Optional.empty());

        // When
        stadiumService.findById(1L);
    }

    @Test
    public void convertDtoToEntityConvertsCorrectly() throws Exception {
        StadiumDto stadiumDto = new StadiumDto("Test stadium", "Test city", 40000);

        // Expected object
        Stadium stadium = new Stadium(stadiumDto.getName(), stadiumDto.getCity(), stadiumDto.getCapacity());

        // When
        Stadium convertedStadium = stadiumService.convertDtoToEntity(stadiumDto);

        // Then
        assertThat(convertedStadium.getId()).isNull();
        assertThat(convertedStadium.getName()).isEqualTo(stadium.getName());
        assertThat(convertedStadium.getCity()).isEqualTo(stadium.getCity());
        assertThat(convertedStadium.getCapacity()).isEqualTo(stadium.getCapacity());
    }

    @Test
    public void deleteByIdReturnsCorrectResponseWhenResourceAlreadyDeleted() throws Exception {
        // Given
        given(stadiumRepository.existsById(any())).willReturn(false);

        // When
        boolean response = stadiumService.deleteById(1L);

        // Then
        assertThat(response).isTrue();
    }

    @Test
    public void deleteByIdReturnsCorrectResponseAfterDeletingResource() throws Exception {
        // Given
        given(stadiumRepository.existsById(any())).willReturn(true, false);

        // When
        boolean response = stadiumService.deleteById(1L);

        // Then
        assertThat(response).isTrue();
    }
}
