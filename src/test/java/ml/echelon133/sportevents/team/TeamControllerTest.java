package ml.echelon133.sportevents.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.exception.APIExceptionHandler;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.*;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeamService teamService;

    @Mock
    private TeamResourceAssembler teamResourceAssembler;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @InjectMocks
    private TeamController teamController;

    private JacksonTester<TeamDto> jsonTeamDto;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mockMvc = MockMvcBuilders.standaloneSetup(teamController).setControllerAdvice(exceptionHandler).build();
    }

    private League buildLeague(Long id, String leagueName, String leagueCountry) {
        League testLeague = new League(leagueName, leagueCountry);
        testLeague.setId(id);
        return testLeague;
    }

    private Team buildTeam(Long id, String teamName, League league) {
        Team testTeam = new Team(teamName, league);
        testTeam.setId(id);
        return testTeam;
    }

    private TeamResource buildTeamResource(Team team) throws Exception {
        LeagueResource leagueResource = new LeagueResource(team.getLeague(),
                linkTo(LeagueController.class).withRel("leagues"),
                linkTo(methodOn(LeagueController.class).getLeague(team.getLeague().getId())).withSelfRel());
        return new TeamResource(team,
                                leagueResource,
                                linkTo(TeamController.class).withRel("teams"),
                                linkTo(methodOn(TeamController.class).getTeam(team.getId())).withSelfRel());
    }

    @Test
    public void getTeamsReturnsEmptyResourcesCorrectly() throws Exception {
        // Given
        given(teamService.findAll()).willReturn(Collections.emptyList());

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/teams")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext responseJson = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseJson.read("$.links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");
        assertThat(responseJson.read("$.content").toString()).isEqualTo("[]");
    }

    @Test
    public void getTeamsReturnsFilteredResourcesCorrectly() throws Exception {
        League testLeague = buildLeague(1L, "test league", "test country");
        Team testTeam = buildTeam(1L, "test team", testLeague);
        TeamResource teamResource = buildTeamResource(testTeam);

        List<Team> teams = Collections.singletonList(testTeam);
        List<TeamResource> teamResources = Collections.singletonList(teamResource);

        // Given
        given(teamService.findAllByNameContaining("test")).willReturn(teams);
        given(teamResourceAssembler.toResources(teams)).willReturn(teamResources);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/teams")
                .param("nameContains", "test")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");

        assertThat(json.read("$.content[0].id").toString()).isEqualTo(testTeam.getId().toString());
        assertThat(json.read("$.content[0].name").toString()).isEqualTo(testTeam.getName());
        assertThat(json.read("$.content[0].links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");

        assertThat(json.read("$.content[0].league.id").toString()).isEqualTo(testTeam.getLeague().getId().toString());
        assertThat(json.read("$.content[0].league.name").toString()).isEqualTo(testTeam.getLeague().getName());
        assertThat(json.read("$.content[0].league.country").toString()).isEqualTo(testTeam.getLeague().getCountry());
        assertThat(json.read("$.content[0].league.links[?(@.rel=='leagues')].href").toString())
                .contains("/api\\/leagues");
        assertThat(json.read("$.content[0].league.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/leagues\\/" + testTeam.getLeague().getId().toString());
    }

    @Test
    public void getTeamsReturnsAllResourcesCorrectly() throws Exception {
        League testLeague = buildLeague(5L, "test league 2", "test country 2");
        Team testTeam = buildTeam(8L, "test team 2", testLeague);
        TeamResource teamResource = buildTeamResource(testTeam);

        List<Team> teams = Collections.singletonList(testTeam);
        List<TeamResource> teamResources = Collections.singletonList(teamResource);

        // Given
        given(teamService.findAll()).willReturn(teams);
        given(teamResourceAssembler.toResources(teams)).willReturn(teamResources);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/teams")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.read("$.links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");

        assertThat(json.read("$.content[0].id").toString()).isEqualTo(testTeam.getId().toString());
        assertThat(json.read("$.content[0].name").toString()).isEqualTo(testTeam.getName());
        assertThat(json.read("$.content[0].links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");

        assertThat(json.read("$.content[0].league.id").toString()).isEqualTo(testTeam.getLeague().getId().toString());
        assertThat(json.read("$.content[0].league.name").toString()).isEqualTo(testTeam.getLeague().getName());
        assertThat(json.read("$.content[0].league.country").toString()).isEqualTo(testTeam.getLeague().getCountry());
        assertThat(json.read("$.content[0].league.links[?(@.rel=='leagues')].href").toString())
                .contains("/api\\/leagues");
        assertThat(json.read("$.content[0].league.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/leagues\\/" + testTeam.getLeague().getId().toString());
    }

    @Test
    public void getTeamReturnsCorrectResponseWhenResourceDoesNotExist() throws Exception {
        // Given
        given(teamService.findById(anyLong())).willThrow(new ResourceDoesNotExistException("Team with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/teams/1")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Team with this id does not exist");
    }

    @Test
    public void getTeamReturnsExistingResourceCorrectly() throws Exception {
        League testLeague = buildLeague(1L, "testtest", "country");
        Team testTeam = buildTeam(3L, "test name", testLeague);
        TeamResource teamResource = buildTeamResource(testTeam);

        // Given
        given(teamService.findById(3L)).willReturn(testTeam);
        given(teamResourceAssembler.toResource(testTeam)).willReturn(teamResource);

        // When
        MockHttpServletResponse response = mockMvc.perform(get("/api/teams/3")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        assertThat(json.read("$.id").toString()).isEqualTo(testTeam.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(testTeam.getName());
        assertThat(json.read("$.links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/teams\\/" + testTeam.getId());

        assertThat(json.read("$.league.id").toString()).isEqualTo(testTeam.getLeague().getId().toString());
        assertThat(json.read("$.league.name").toString()).isEqualTo(testTeam.getLeague().getName());
        assertThat(json.read("$.league.country").toString()).isEqualTo(testTeam.getLeague().getCountry());
        assertThat(json.read("$.league.links[?(@.rel=='leagues')].href").toString())
                .contains("/api\\/leagues");
        assertThat(json.read("$.league.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/leagues\\/" + testTeam.getLeague().getId().toString());
    }

    @Test
    public void createTeamTeamDtoNullFieldsAreValidated() throws Exception {
        TeamDto teamDto = new TeamDto(null, null);

        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/teams")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("leagueId validation error: must not be null");
        assertThat(response.getContentAsString()).contains("name validation error: must not be null");
    }

    @Test
    public void createTeamTeamDtoFieldLengthsAreValidated() throws Exception {
        TeamDto teamDto = new TeamDto("t", 5L);

        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/teams")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name validation error: length must be between 2 and 200");
    }

    @Test
    public void createTeamReturnsCorrectResponseWhenDtoConversionFails() throws Exception {
        TeamDto teamDto = new TeamDto("teamname", 5L);

        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // Given
        given(teamService.convertDtoToEntity(any(TeamDto.class)))
                .willThrow(new ResourceDoesNotExistException("League with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/teams")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("League with this id does not exist");
    }

    @Test
    public void createTeamReturnsCorrectResponseOnSuccess() throws Exception {
        League testLeague = buildLeague(1L, "league name", "league country");
        Team testTeam = buildTeam(1L, "team name", testLeague);
        TeamResource teamResource = buildTeamResource(testTeam);

        TeamDto teamDto = new TeamDto(testTeam.getName(), testLeague.getId());

        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // Given
        given(teamService.convertDtoToEntity(
                argThat(arg -> arg.getName().equals(teamDto.getName())
                        &&
                        arg.getLeagueId().longValue() == teamDto.getLeagueId()))).willReturn(testTeam);
        given(teamService.save(testTeam)).willReturn(testTeam);
        given(teamResourceAssembler.toResource(testTeam)).willReturn(teamResource);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/teams")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        assertThat(json.read("$.id").toString()).isEqualTo(testTeam.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(testTeam.getName());
        assertThat(json.read("$.links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/teams\\/" + testTeam.getId());

        assertThat(json.read("$.league.id").toString()).isEqualTo(testTeam.getLeague().getId().toString());
        assertThat(json.read("$.league.name").toString()).isEqualTo(testTeam.getLeague().getName());
        assertThat(json.read("$.league.country").toString()).isEqualTo(testTeam.getLeague().getCountry());
        assertThat(json.read("$.league.links[?(@.rel=='leagues')].href").toString())
                .contains("/api\\/leagues");
        assertThat(json.read("$.league.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/leagues\\/" + testTeam.getLeague().getId().toString());
    }

    @Test
    public void replaceTeamTeamDtoNullFieldsAreValidated() throws Exception {
        TeamDto teamDto = new TeamDto(null, null);

        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/teams/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("leagueId validation error: must not be null");
        assertThat(response.getContentAsString()).contains("name validation error: must not be null");
    }

    @Test
    public void replaceTeamTeamDtoFieldLengthsAreValidated() throws Exception {
        TeamDto teamDto = new TeamDto("t", 5L);

        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/teams/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name validation error: length must be between 2 and 200");
    }

    @Test
    public void replaceTeamReturnsCorrectResponseWhenDtoConversionFails() throws Exception {
        TeamDto teamDto = new TeamDto("teamname", 5L);

        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // Given
        given(teamService.convertDtoToEntity(any(TeamDto.class)))
                .willThrow(new ResourceDoesNotExistException("League with this id does not exist"));

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/teams/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("League with this id does not exist");
    }

    @Test
    public void replaceTeamReturnsCorrectResponseOnSuccess() throws Exception {
        League originalLeague = buildLeague(1L, "league name", "league country");
        Team originalTeam = buildTeam(1L, "team name", originalLeague);

        League replacementLeague = buildLeague(2L, "new league", "test text");
        Team replacementTeam = buildTeam(1L, "new name", replacementLeague);
        TeamResource teamResource = buildTeamResource(replacementTeam);

        TeamDto teamDto = new TeamDto(replacementTeam.getName(), replacementLeague.getId());
        JsonContent<TeamDto> teamDtoJsonContent = jsonTeamDto.write(teamDto);

        // Given
        given(teamService.findById(1L)).willReturn(originalTeam);
        given(teamService.convertDtoToEntity(
                argThat(arg -> arg.getName().equals(teamDto.getName())
                        &&
                        arg.getLeagueId().longValue() == teamDto.getLeagueId()))).willReturn(replacementTeam);
        given(teamService.save(replacementTeam)).willReturn(replacementTeam);
        given(teamResourceAssembler.toResource(replacementTeam)).willReturn(teamResource);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/teams/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamDtoJsonContent.getJson())
        ).andReturn().getResponse();

        // Then
        DocumentContext json = JsonPath.parse(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        assertThat(json.read("$.id").toString()).isEqualTo(replacementTeam.getId().toString());
        assertThat(json.read("$.name").toString()).isEqualTo(replacementTeam.getName());
        assertThat(json.read("$.links[?(@.rel=='teams')].href").toString()).contains("/api\\/teams");
        assertThat(json.read("$.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/teams\\/" + replacementTeam.getId());

        assertThat(json.read("$.league.id").toString()).isEqualTo(replacementTeam.getLeague().getId().toString());
        assertThat(json.read("$.league.name").toString()).isEqualTo(replacementTeam.getLeague().getName());
        assertThat(json.read("$.league.country").toString()).isEqualTo(replacementTeam.getLeague().getCountry());
        assertThat(json.read("$.league.links[?(@.rel=='leagues')].href").toString())
                .contains("/api\\/leagues");
        assertThat(json.read("$.league.links[?(@.rel=='self')].href").toString())
                .contains("/api\\/leagues\\/" + replacementTeam.getLeague().getId().toString());
    }

    @Test
    public void deleteTeamReturnsCorrectResponseAfterDeletingResource() throws Exception {
        // Given
        given(teamService.deleteById(any())).willReturn(true);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                delete("/api/teams/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"deleted\":true}");
    }
}
