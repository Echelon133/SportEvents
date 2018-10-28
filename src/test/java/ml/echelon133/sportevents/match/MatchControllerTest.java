package ml.echelon133.sportevents.match;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ml.echelon133.sportevents.exception.APIExceptionHandler;
import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.league.LeagueController;
import ml.echelon133.sportevents.league.LeagueResource;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.stadium.StadiumController;
import ml.echelon133.sportevents.stadium.StadiumResource;
import ml.echelon133.sportevents.team.Team;
import ml.echelon133.sportevents.team.TeamController;
import ml.echelon133.sportevents.team.TeamResource;
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
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
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

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(matchController).setControllerAdvice(exceptionHandler).build();
    }

    private Stadium buildStadium(Long id, String stadiumName, String stadiumCity, Integer stadiumCapacity) {
        Stadium testStadium = new Stadium(stadiumName, stadiumCity, stadiumCapacity);
        testStadium.setId(id);
        return testStadium;
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

    private Match buildMatch(Long id, Team teamA, Team teamB, League league, Stadium stadium) {
        Match testMatch = new Match(new Date(), teamA, teamB, league, stadium);
        testMatch.setId(id);
        return testMatch;
    }

    private MatchResource buildMatchResource(Match match) throws Exception {
        LeagueResource leagueResource;
        StadiumResource stadiumResource;

        try {
            leagueResource = new LeagueResource(match.getLeague(),
                    linkTo(LeagueController.class).withRel("leagues"),
                    linkTo(methodOn(LeagueController.class).getLeague(match.getLeague().getId())).withSelfRel());
        } catch (NullPointerException ex) {
            leagueResource = null;
        }

        try {
            stadiumResource = new StadiumResource(match.getStadium(),
                    linkTo(StadiumController.class).withRel("stadiums"),
                    linkTo(methodOn(StadiumController.class).getStadium(match.getStadium().getId())).withSelfRel());
        } catch (NullPointerException ex) {
            stadiumResource = null;
        }

        TeamResource teamAResource = new TeamResource(match.getTeamA(), leagueResource,
                linkTo(TeamController.class).withRel("teams"),
                linkTo(methodOn(TeamController.class).getTeam(match.getTeamA().getId())).withSelfRel());

        TeamResource teamBResource = new TeamResource(match.getTeamB(), leagueResource,
                linkTo(TeamController.class).withRel("teams"),
                linkTo(methodOn(TeamController.class).getTeam(match.getTeamB().getId())).withSelfRel());

        return new MatchResource(match, teamAResource, teamBResource, leagueResource, stadiumResource,
                linkTo(MatchController.class).withRel("matches"),
                linkTo(methodOn(MatchController.class).getMatch(match.getId())).withSelfRel());
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
}
