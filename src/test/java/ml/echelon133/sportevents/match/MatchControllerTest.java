package ml.echelon133.sportevents.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.exception.APIExceptionHandler;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.team.Team;

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

import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import static ml.echelon133.sportevents.TestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MatchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatchService matchService;

    @Mock
    private MatchResourceAssembler matchResourceAssembler;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @InjectMocks
    private MatchController matchController;

    private JacksonTester<MatchDto> jsonMatchDto;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mockMvc = MockMvcBuilders.standaloneSetup(matchController).setControllerAdvice(exceptionHandler).build();
    }

    @Test
    public void getMatchesReturnsEmptyResourcesCorrectly() throws Exception {
        // Given
        given(matchService.findAll()).willReturn(Collections.emptyList());

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext responseJson = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseJson.read("$.links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(responseJson.read("$.links[?(@.rel=='leagues')].href").toString()).contains("/api\\/leagues");
        assertThat(responseJson.read("$.links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");
        assertThat(responseJson.read("$.links[?(@.rel=='stadiums')].href").toString()).contains("/api\\/stadiums");
        assertThat(responseJson.read("$.content").toString()).isEqualTo("[]");
    }

    @Test
    public void getMatchesReturnsResourcesFilteredByStatusCorrectly() throws Exception {
        League league = buildLeague(1L, "Test league", "Test country");
        Stadium stadium = buildStadium(1L, "Test stadium", "Test city", 20000);
        Team teamA = buildTeam(1L, "TeamA", league);
        Team teamB = buildTeam(2L, "TeamB", league);
        Match match = buildMatch(1L, teamA, teamB, league, stadium);
        MatchResource matchResource = buildMatchResource(match);

        List<Match> filteredMatches = Collections.singletonList(match);
        List<MatchResource> matchResources = Collections.singletonList(matchResource);

        // Given
        given(matchService.findAllWithStatus("NOT_STARTED")).willReturn(filteredMatches);
        given(matchResourceAssembler.toResources(filteredMatches)).willReturn(matchResources);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches")
                .param("status", "NOT_STARTED")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.content[0].id").toString()).isEqualTo(match.getId().toString());
        assertThat(json.read("$.content[0].status").toString()).isEqualTo(match.getStatus().toString());
        assertThat(json.read("$.content[0].teamA.name").toString()).isEqualTo(match.getTeamA().getName());
        assertThat(json.read("$.content[0].teamB.name").toString()).isEqualTo(match.getTeamB().getName());
        assertThat(json.read("$.content[0].league.name").toString()).isEqualTo(match.getLeague().getName());
        assertThat(json.read("$.content[0].stadium.name").toString()).isEqualTo(match.getStadium().getName());
        assertThat(json.read("$.content[0].links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.content[0].links[?(@.rel=='self')].href").toString())
                .contains("/api\\/matches\\/" + match.getId());
    }

    @Test
    public void getMatchesReturnsResourcesFilteredByDateCorrectly() throws Exception {
        League league = buildLeague(1L, "Test league", "Test country");
        Team teamA = buildTeam(1L, "TeamA", league);
        Team teamB = buildTeam(2L, "TeamB", league);
        Match match = buildMatch(1L, teamA, teamB, league, null);
        MatchResource matchResource = buildMatchResource(match);

        List<Match> filteredMatches = Collections.singletonList(match);
        List<MatchResource> matchResources = Collections.singletonList(matchResource);

        // Given
        given(matchService.findAllWithDateWithin("THREE_DAYS")).willReturn(filteredMatches);
        given(matchResourceAssembler.toResources(filteredMatches)).willReturn(matchResources);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches")
                .param("dateWithin", "THREE_DAYS")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.content[0].id").toString()).isEqualTo(match.getId().toString());
        assertThat(json.read("$.content[0].status").toString()).isEqualTo(match.getStatus().toString());
        assertThat(json.read("$.content[0].teamA.name").toString()).isEqualTo(match.getTeamA().getName());
        assertThat(json.read("$.content[0].teamB.name").toString()).isEqualTo(match.getTeamB().getName());
        assertThat(json.read("$.content[0].league.name").toString()).isEqualTo(match.getLeague().getName());
        assertThat(json.read("$.content[0].links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.content[0].links[?(@.rel=='self')].href").toString())
                .contains("/api\\/matches\\/" + match.getId());
    }

    @Test
    public void getMatchesReturnsAllResourcesCorrectly() throws Exception {
        League league = buildLeague(1L, "Test league", "Test country");
        Team teamA = buildTeam(1L, "TeamA", league);
        Team teamB = buildTeam(2L, "TeamB", league);
        Match match = buildMatch(1L, teamA, teamB, league, null);
        MatchResource matchResource = buildMatchResource(match);

        List<Match> allMatches = Collections.singletonList(match);
        List<MatchResource> allResources = Collections.singletonList(matchResource);

        // Given
        given(matchService.findAll()).willReturn(allMatches);
        given(matchResourceAssembler.toResources(allMatches)).willReturn(allResources);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.content[0].id").toString()).isEqualTo(match.getId().toString());
        assertThat(json.read("$.content[0].status").toString()).isEqualTo(match.getStatus().toString());
        assertThat(json.read("$.content[0].teamA.name").toString()).isEqualTo(match.getTeamA().getName());
        assertThat(json.read("$.content[0].teamB.name").toString()).isEqualTo(match.getTeamB().getName());
        assertThat(json.read("$.content[0].league.name").toString()).isEqualTo(match.getLeague().getName());
        assertThat(json.read("$.content[0].links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.content[0].links[?(@.rel=='self')].href").toString())
                .contains("/api\\/matches\\/" + match.getId());
    }

    @Test
    public void getMatchReturnsCorrectResponseWhenResourceDoesNotExist() throws Exception {
        // Given
        given(matchService.findById(anyLong())).willThrow(new ResourceDoesNotExistException("Match with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Match with this id does not exist");
    }

    @Test
    public void getMatchReturnsExistingResourceCorrectly() throws Exception {
        League league = buildLeague(1L, "Test league", "Test country");
        Team teamA = buildTeam(15L, "Test TeamA", league);
        Team teamB = buildTeam(25L, "Test TeamB", league);
        Match match = buildMatch(7L, teamA, teamB, null, null);
        MatchResource matchResource = buildMatchResource(match);

        // Given
        given(matchService.findById(7L)).willReturn(match);
        given(matchResourceAssembler.toResource(match)).willReturn(matchResource);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/matches/7")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/matches\\/" + match.getId());

        assertThat(json.read("$.id").toString()).isEqualTo(match.getId().toString());
        assertThat(json.read("$.status").toString()).isEqualTo(match.getStatus().toString());
        assertThat(json.read("$.teamA.name").toString()).isEqualTo(match.getTeamA().getName());
        assertThat(json.read("$.teamB.name").toString()).isEqualTo(match.getTeamB().getName());
    }

    @Test
    public void createMatchMatchDtoInvalidStartDateResponseIsCorrect() throws Exception {
        MatchDto matchDto = new MatchDto("2018-99-100 asdf", 1L, 2L);

        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // Given
        given(matchService.convertDtoToEntity(any())).willThrow(new DateTimeParseException("Parse error", "asdf", 0));

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Date could not be parsed");
    }

    @Test
    public void createMatchMatchDtoNullFieldsAreValidated() throws Exception {
        MatchDto matchDto = new MatchDto(null, null, null);

        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("startDate validation error: must not be null");
        assertThat(response.getContentAsString()).contains("teamA validation error: must not be null");
        assertThat(response.getContentAsString()).contains("teamB validation error: must not be null");
    }

    @Test
    public void createMatchMatchDtoTeamIdsAreValidated() throws Exception {
        MatchDto matchDto = new MatchDto("2018-12-31 20:00", 1L, 1L);

        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("matchDto validation error: Team IDs cannot be equal");
    }

    @Test
    public void createMatchReturnsCorrectResponseOnSuccess() throws Exception {
        League league = buildLeague(1L, "League", "Country");
        Team teamA = buildTeam(1L, "First team", league);
        Team teamB = buildTeam(3L, "Second team", league);
        Match match = buildMatch(7L, teamA, teamB, null, null);
        MatchResource matchResource = buildMatchResource(match);

        MatchDto matchDto = new MatchDto("2018-12-01 16:00", 1L, 3L);
        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // Given
        given(matchService.convertDtoToEntity(
                argThat(a -> a.getStartDate().equals(matchDto.getStartDate())
                        &&
                        a.getTeamA().equals(match.getTeamA().getId())
                        &&
                        a.getTeamB().equals(match.getTeamB().getId())))).willReturn(match);
        given(matchService.save(match)).willReturn(match);
        given(matchResourceAssembler.toResource(match)).willReturn(matchResource);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(json.read("$.links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/matches\\/" + match.getId());

        assertThat(json.read("$.id").toString()).isEqualTo(match.getId().toString());
        assertThat(json.read("$.status").toString()).isEqualTo(match.getStatus().toString());
        assertThat(json.read("$.teamA.name").toString()).isEqualTo(match.getTeamA().getName());
        assertThat(json.read("$.teamB.name").toString()).isEqualTo(match.getTeamB().getName());
    }

    @Test
    public void replaceMatchReturnsCorrectResponseIfMatchDoesNotExist() throws Exception {
        MatchDto matchDto = new MatchDto("2018-99-100 asdf", 1L, 2L);
        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // Given
        given(matchService.findById(1L)).willThrow(new ResourceDoesNotExistException("Match with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/matches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Match with this id does not exist");
    }

    @Test
    public void replaceMatchMatchDtoInvalidStartDateResponseIsCorrect() throws Exception {
        MatchDto matchDto = new MatchDto("2018-99-100 asdf", 1L, 2L);

        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // Given
        given(matchService.convertDtoToEntity(any())).willThrow(new DateTimeParseException("Parse error", "asdf", 0));

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/matches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Date could not be parsed");
    }

    @Test
    public void replaceMatchMatchDtoNullFieldsAreValidated() throws Exception {
        MatchDto matchDto = new MatchDto(null, null, null);

        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/matches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("startDate validation error: must not be null");
        assertThat(response.getContentAsString()).contains("teamA validation error: must not be null");
        assertThat(response.getContentAsString()).contains("teamB validation error: must not be null");
    }

    @Test
    public void replaceMatchMatchDtoTeamIdsAreValidated() throws Exception {
        MatchDto matchDto = new MatchDto("2018-12-31 20:00", 1L, 1L);

        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/matches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("matchDto validation error: Team IDs cannot be equal");
    }

    @Test
    public void replaceMatchReturnsCorrectResponseOnSuccess() throws Exception {
        League league = buildLeague(1L, "League", "Country");
        Team teamA = buildTeam(1L, "First team", league);
        Team teamB = buildTeam(3L, "Second team", league);
        Team teamC = buildTeam(5L, "Third team", league);
        Match match = buildMatch(7L, teamA, teamB, null, null);
        Match changedMatch = buildMatch(7L, teamA, teamC, null, null);
        MatchResource changedMatchResource = buildMatchResource(changedMatch);

        MatchDto matchDto = new MatchDto("2018-12-01 16:00", 1L, 5L);
        JsonContent<MatchDto> matchDtoJsonContent = jsonMatchDto.write(matchDto);

        // Given
        given(matchService.findById(7L)).willReturn(match);
        given(matchService.convertDtoToEntity(
                argThat(a -> a.getStartDate().equals(matchDto.getStartDate())
                        &&
                        a.getTeamA().equals(changedMatch.getTeamA().getId())
                        &&
                        a.getTeamB().equals(changedMatch.getTeamB().getId())))).willReturn(changedMatch);
        given(matchService.save(changedMatch)).willReturn(changedMatch);
        given(matchResourceAssembler.toResource(changedMatch)).willReturn(changedMatchResource);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/matches/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchDtoJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='matches')].href").toString()).contains("/api\\/matches");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/matches\\/" + match.getId());

        assertThat(json.read("$.id").toString()).isEqualTo(changedMatch.getId().toString());
        assertThat(json.read("$.status").toString()).isEqualTo(changedMatch.getStatus().toString());
        assertThat(json.read("$.teamA.name").toString()).isEqualTo(changedMatch.getTeamA().getName());
        assertThat(json.read("$.teamB.name").toString()).isEqualTo(changedMatch.getTeamB().getName());
    }

    @Test
    public void deleteMatchReturnsCorrectResponseAfterDeletingResource() throws Exception {
        // Given
        given(matchService.deleteById(any())).willReturn(true);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                delete("/api/matches/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"deleted\":true}");
    }
}
