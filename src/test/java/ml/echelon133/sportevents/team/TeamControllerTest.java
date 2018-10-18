package ml.echelon133.sportevents.team;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.exception.APIExceptionHandler;
import ml.echelon133.sportevents.league.*;
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
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    private LeagueService leagueService;

    @Mock
    private TeamResourceAssembler teamResourceAssembler;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @InjectMocks
    private TeamController teamController;

    @Before
    public void setup() {
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
        return new TeamResource(team, leagueResource, linkTo(TeamController.class).withRel("teams"));
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
}
