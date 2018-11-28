package ml.echelon133.sportevents;

import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.league.LeagueResource;
import ml.echelon133.sportevents.league.LeagueResourceAssembler;
import ml.echelon133.sportevents.match.Match;
import ml.echelon133.sportevents.match.MatchResource;
import ml.echelon133.sportevents.match.MatchResourceAssembler;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.stadium.StadiumResourceAssembler;
import ml.echelon133.sportevents.team.Team;
import ml.echelon133.sportevents.team.TeamResource;
import ml.echelon133.sportevents.team.TeamResourceAssembler;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;


public class TestUtils {

    private static LeagueResourceAssembler leagueResourceAssembler;
    private static TeamResourceAssembler teamResourceAssembler;
    private static MatchResourceAssembler matchResourceAssembler;
    private static StadiumResourceAssembler stadiumResourceAssembler;
    private static Random random;

    static {
        leagueResourceAssembler = new LeagueResourceAssembler();
        stadiumResourceAssembler = new StadiumResourceAssembler();
        teamResourceAssembler = new TeamResourceAssembler(leagueResourceAssembler);
        matchResourceAssembler = new MatchResourceAssembler(teamResourceAssembler, leagueResourceAssembler, stadiumResourceAssembler);
        random = new Random();
    }

    public static Stadium buildStadium(Long id, String stadiumName, String stadiumCity, Integer stadiumCapacity) {
        Stadium testStadium = new Stadium(stadiumName, stadiumCity, stadiumCapacity);
        testStadium.setId(id);
        return testStadium;
    }

    public static League buildLeague(Long id, String leagueName, String leagueCountry) {
        League testLeague = new League(leagueName, leagueCountry);
        testLeague.setId(id);
        return testLeague;
    }

    public static Team buildTeam(Long id, String teamName, League league) {
        Team testTeam = new Team(teamName, league);
        testTeam.setId(id);
        return testTeam;
    }

    public static Match buildMatch(Long id, Team teamA, Team teamB, League league, Stadium stadium) {
        Match testMatch = new Match(new Date(), teamA, teamB, league, stadium);
        testMatch.setId(id);
        testMatch.getTeamA().addMatch(testMatch);
        testMatch.getTeamB().addMatch(testMatch);
        return testMatch;
    }

    public static Match getRandomMatch() {
        long matchId = random.nextLong();
        long leagueId = random.nextLong();
        long teamAId = random.nextLong();
        long teamBId = random.nextLong();
        String leagueName = getRandomString();
        String teamAName = getRandomString();
        String teamBName = getRandomString();

        League league = buildLeague(leagueId, leagueName, "Country");
        Team teamA = buildTeam(teamAId, teamAName, league);
        Team teamB = buildTeam(teamBId, teamBName, league);
        return buildMatch(matchId, teamA, teamB, league, null);
    }

    public static Match getRandomMatch(Long matchId) {
        long leagueId = random.nextLong();
        long teamAId = random.nextLong();
        long teamBId = random.nextLong();
        String leagueName = getRandomString();
        String teamAName = getRandomString();
        String teamBName = getRandomString();

        League league = buildLeague(leagueId, leagueName, "Country");
        Team teamA = buildTeam(teamAId, teamAName, league);
        Team teamB = buildTeam(teamBId, teamBName, league);
        return buildMatch(matchId, teamA, teamB, league, null);
    }

    private static String getRandomString() {
        int length = random.nextInt(20);
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return new String(bytes, Charset.defaultCharset());
    }

    public static TeamResource buildTeamResource(Team team) {
        return teamResourceAssembler.toResource(team);
    }

    public static LeagueResource buildLeagueResource(League league) {
        return leagueResourceAssembler.toResource(league);
    }

    public static MatchResource buildMatchResource(Match match) {
        return matchResourceAssembler.toResource(match);
    }

}
