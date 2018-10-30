package ml.echelon133.sportevents.match;

import ml.echelon133.sportevents.exception.ResourceDoesNotExistException;
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
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
        MatchResource matchResource;

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

        try {
            matchResource = new MatchResource(entity, teamA, teamB, league, stadium,
                    linkTo(MatchController.class).withRel("matches"),
                    linkTo(methodOn(MatchController.class).getMatch(entity.getId())).withSelfRel());
        } catch (ResourceDoesNotExistException ex) {
            // getMatch throws Exception only if resource (Match) does not exist
            // getMatch(Long id) is used here only to generate a link to a resource (methodOn creates a proxy)
            // see here: https://docs.spring.io/spring-hateoas/docs/current/api/org/springframework/hateoas/mvc/ControllerLinkBuilder.html#methodOn-java.lang.Class-java.lang.Object...-

            // this exception handling is needed only for syntactic purposes
            // see here: https://github.com/spring-projects/spring-hateoas/issues/82

            // also see: https://github.com/search?q=This+should+never+happen&type=Code&utf8=%E2%9C%93
            throw new RuntimeException("getMatch threw an exception - this should never happen");
        }

        return matchResource;
    }
}
