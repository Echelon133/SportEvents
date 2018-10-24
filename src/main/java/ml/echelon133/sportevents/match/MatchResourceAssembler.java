package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.league.LeagueResource;
import ml.echelon133.sportevents.league.LeagueResourceAssembler;
import ml.echelon133.sportevents.stadium.StadiumResource;
import ml.echelon133.sportevents.stadium.StadiumResourceAssembler;
import ml.echelon133.sportevents.team.TeamResource;
import ml.echelon133.sportevents.team.TeamResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class MatchResourceAssembler extends ResourceAssemblerSupport<Match, MatchResource> {

    private TeamResourceAssembler teamResourceAssembler;
    private LeagueResourceAssembler leagueResourceAssembler;
    private StadiumResourceAssembler stadiumResourceAssembler;

    @Autowired
    public MatchResourceAssembler(TeamResourceAssembler teamResourceAssembler,
                                  LeagueResourceAssembler leagueResourceAssembler,
                                  StadiumResourceAssembler stadiumResourceAssembler) {

        super(MatchController.class, MatchResource.class);
        this.teamResourceAssembler = teamResourceAssembler;
        this.leagueResourceAssembler = leagueResourceAssembler;
        this.stadiumResourceAssembler = stadiumResourceAssembler;
    }

    @Override
    public MatchResource toResource(Match entity) {
        TeamResource teamA = teamResourceAssembler.toResource(entity.getTeamA());
        TeamResource teamB = teamResourceAssembler.toResource(entity.getTeamB());
        LeagueResource league;
        StadiumResource stadium;

        // entity.getLeague() can be null (league field is not required)
        try {
            league = leagueResourceAssembler.toResource(entity.getLeague());
        } catch (Exception ex) {
            league = null;
        }

        // entity.getStadium() can be null (stadium field is not required)
        try {
            stadium = stadiumResourceAssembler.toResource(entity.getStadium());
        } catch (Exception ex) {
            stadium = null;
        }

        return new MatchResource(entity, teamA, teamB,
                                 league, stadium, linkTo(MatchController.class).withRel("matches"));
    }
}
