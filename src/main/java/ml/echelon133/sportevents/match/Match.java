package ml.echelon133.sportevents.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import ml.echelon133.sportevents.league.League;
import ml.echelon133.sportevents.stadium.Stadium;
import ml.echelon133.sportevents.team.Team;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "football_match")
public class Match {

    public enum Status {
        NOT_STARTED, FIRST_HALF, SECOND_HALF,
        BREAK_TIME, OT_FIRST_HALF, OT_SECOND_HALF,
        PENALTIES, FINISHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date startDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name="teamA_id")
    private Team teamA;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name="teamB_id")
    private Team teamB;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="league_id")
    private League league;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="stadium_id")
    private Stadium stadium;

    @OneToMany(mappedBy = "match", cascade = CascadeType.MERGE)
    @JsonIgnore
    @OrderBy("id DESC")
    private List<AbstractMatchEvent> events;

    @Embedded
    private ScoreInfo result;

    public Match() {
        this.result = new ScoreInfo();
        this.status = Status.NOT_STARTED;
        this.events = new ArrayList<>();
    }

    public Match(Date startDate, Team teamA, Team teamB) {
        this();
        this.startDate = startDate;
        setTeamA(teamA);
        setTeamB(teamB);
    }

    public Match(Date startDate, Team teamA, Team teamB, League league) {
        this(startDate, teamA, teamB);
        this.league = league;
    }

    public Match(Date startDate, Team teamA, Team teamB, League league, Stadium stadium) {
        this(startDate, teamA, teamB, league);
        this.stadium = stadium;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team teamA) {
        teamA.addMatch(this);
        this.teamA = teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team teamB) {
        teamB.addMatch(this);
        this.teamB = teamB;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public ScoreInfo getResult() {
        return result;
    }

    public void setResult(ScoreInfo result) {
        this.result = result;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<AbstractMatchEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AbstractMatchEvent> events) {
        this.events = events;
    }

    public void addEvent(AbstractMatchEvent event) {
        event.setMatch(this);
        this.events.add(event);
    }
}
