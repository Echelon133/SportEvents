package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.league.LeagueResource;
import ml.echelon133.sportevents.stadium.StadiumResource;
import ml.echelon133.sportevents.team.TeamResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class MatchResource extends Resource<Match> {

    private TeamResource teamA;
    private TeamResource teamB;
    private LeagueResource league;
    private StadiumResource stadium;

    public MatchResource(Match content, Link... links) {
        super(content, links);
    }

    public MatchResource(Match content,
                         TeamResource teamAResource,
                         TeamResource teamBResource,
                         LeagueResource leagueResource,
                         StadiumResource stadiumResource,
                         Link... links) {

        super(content, links);
        this.teamA = teamAResource;
        this.teamB = teamBResource;
        this.league = leagueResource;
        this.stadium = stadiumResource;
    }

    public TeamResource getTeamA() {
        return teamA;
    }

    public TeamResource getTeamB() {
        return teamB;
    }

    public LeagueResource getLeague() {
        return league;
    }

    public StadiumResource getStadium() {
        return stadium;
    }
}
