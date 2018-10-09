package ml.echelon133.sportevents.league;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.exception.APIExceptionHandler;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(MockitoJUnitRunner.class)
public class LeagueControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LeagueService leagueService;

    @Mock
    private LeagueResourceAssembler resourceAssembler;

    @InjectMocks
    private LeagueController leagueController;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(leagueController).setControllerAdvice(exceptionHandler).build();

        // We always use actual toResource/toResources implementation
        given(resourceAssembler.toResource(any(League.class))).willCallRealMethod();
        given(resourceAssembler.toResources(anyIterable())).willCallRealMethod();
    }

    @Test
    public void getLeaguesReturnsEmptyResourcesCorrectly() throws Exception {
        // Given
        given(leagueService.findAll()).willReturn(Collections.emptyList());

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/leagues")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext responseJson = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseJson.read("$.links[?(@.rel=='leagues')].href").toString()).contains("/api\\/leagues");
        assertThat(responseJson.read("$.content").toString()).isEqualTo("[]");
    }

    @Test
    public void getLeaguesReturnsExistingResourcesCorrectly() throws Exception {
        League league = new League("Test league", "Test country");
        league.setId(1L);

        // Given
        given(leagueService.findAll()).willReturn(Arrays.asList(league));

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/leagues")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='leagues')].href").toString()).contains("/api\\/leagues");
        assertThat(json.read("$.content[0].id").toString()).isEqualTo(league.getId().toString());
        assertThat(json.read("$.content[0].name").toString()).isEqualTo(league.getName());
        assertThat(json.read("$.content[0].country").toString()).isEqualTo(league.getCountry());
        assertThat(json.read("$.content[0].links[?(@.rel=='leagues')].href").toString()).contains("/api\\/leagues");
        assertThat(json.read("$.content[0].links[?(@.rel=='self')].href").toString())
                .contains("/api\\/leagues\\/" + league.getId().toString());
    }

    @Test
    public void getLeagueReturnsCorrectResponseWhenResourceDoesNotExist() throws Exception {
        // Given
        given(leagueService.findById(1L)).willThrow(new ResourceDoesNotExistException("League with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/leagues/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("League with this id does not exist");
    }

    @Test
    public void getLeagueReturnsExistingResourceCorrectly() throws Exception {
        League league = new League("Test league", "Test country");
        league.setId(1L);

        // Given
        given(leagueService.findById(1L)).willReturn(league);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/leagues/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='leagues')].href").toString()).contains("/api\\/leagues");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString()).contains("/api\\/leagues\\/" + league.getId());
        assertThat(json.read("$.id").toString()).isEqualTo(league.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(league.getName());
        assertThat(json.read("$.country").toString()).isEqualTo(league.getCountry());
    }
}
