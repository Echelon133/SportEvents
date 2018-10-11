package ml.echelon133.sportevents.stadium;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.exception.APIExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@RunWith(MockitoJUnitRunner.class)
public class StadiumControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StadiumService stadiumService;

    @Mock
    private StadiumResourceAssembler resourceAssembler;

    @InjectMocks
    private StadiumController stadiumController;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(stadiumController).setControllerAdvice(exceptionHandler).build();

        // We always use actual toResource/toResources implementation
        given(resourceAssembler.toResource(any(Stadium.class))).willCallRealMethod();
        given(resourceAssembler.toResources(anyIterable())).willCallRealMethod();
    }

    @Test
    public void getStadiumsReturnsEmptyResourcesCorrectly() throws Exception {
        // Given
        given(stadiumService.findAll()).willReturn(Collections.emptyList());

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/stadiums")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext responseJson = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseJson.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(responseJson.read("$.content").toString()).isEqualTo("[]");
    }

    @Test
    public void getStadiumsReturnsExistingResourcesCorrectly() throws Exception {
        Stadium stadium = new Stadium("Test stadium", "Test city", 40000);
        stadium.setId(1L);

        // Given
        given(stadiumService.findAll()).willReturn(Collections.singletonList(stadium));

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/stadiums")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(json.read("$.content[0].id").toString()).isEqualTo(stadium.getId().toString());
        assertThat(json.read("$.content[0].name").toString()).isEqualTo(stadium.getName());
        assertThat(json.read("$.content[0].city").toString()).isEqualTo(stadium.getCity());
        assertThat(json.read("$.content[0].capacity").toString()).isEqualTo(stadium.getCapacity().toString());
        assertThat(json.read("$.content[0].links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
    }
}
