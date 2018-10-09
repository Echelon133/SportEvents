package ml.echelon133.sportevents.league;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    private JacksonTester<LeagueDto> jsonLeagueDto;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

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

    @Test
    public void createLeagueLeagueDtoNullFieldsAreValidated() throws Exception {
        LeagueDto leagueDto = new LeagueDto(null, null);

        JsonContent<LeagueDto> leagueDtoJsonContent = jsonLeagueDto.write(leagueDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/leagues")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("country validation error: must not be null");
        assertThat(response.getContentAsString()).contains("name validation error: must not be null");
    }

    @Test
    public void createLeagueLeagueDtoFieldLengthsAreValidated() throws Exception {
        LeagueDto leagueDto = new LeagueDto("n", "c");

        JsonContent<LeagueDto> leagueDtoJsonContent = jsonLeagueDto.write(leagueDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/leagues")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("country validation error: length must be between 2 and 30");
        assertThat(response.getContentAsString()).contains("name validation error: length must be between 3 and 50");
    }

    @Test
    public void createLeagueReturnsCorrectResponseWhenLeagueDtoPassesValidation() throws Exception {
        LeagueDto leagueDto = new LeagueDto("name", "country");
        JsonContent<LeagueDto> leagueDtoJsonContent = jsonLeagueDto.write(leagueDto);

        // "Saved" league
        League league = new League(leagueDto.getName(), leagueDto.getCountry());
        league.setId(1L);

        // Given
        given(leagueService.convertDtoToEntity(any(LeagueDto.class))).willReturn(league);
        given(leagueService.save(any(League.class))).willReturn(league);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/leagues")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leagueDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(json.read("$.links[?(@.rel=='leagues')].href").toString()).contains("/api\\/leagues");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString()).contains("/api\\/leagues\\/" + league.getId());
        assertThat(json.read("$.id").toString()).isEqualTo(league.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(league.getName());
        assertThat(json.read("$.country").toString()).isEqualTo(league.getCountry());
    }
}
