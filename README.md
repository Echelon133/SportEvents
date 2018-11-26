# SportEvents Application

Application features:

* OAuth2 authentication system (tokens provided by github)
* Validation of user-provided data
* CRUD operations on all resources (stadiums, teams, matches, leagues)
* Live listening to match events thanks to websockets 

## Documentation

| Endpoint                     | Method     | Data sent with the request | Description                            |
|------------------------      |----------  |----------------------------|-------------                           |
|/api/leagues                  | GET        |                            | Get all leagues                        |
|/api/leagues/{leagueId}       | GET        |                            | Get a league that has specified id     |
|/api/leagues/{leagueId}/teams | GET        |                            | Get all teams of a league that has specified id |
|/api/leagues                  | POST       | League JSON                | Create a new league from json data     |
|/api/leagues/{leagueId}       | PUT        | League JSON                | Replace a league with new league data  |
|/api/leagues/{leagueId}       | DELETE     |                            | Delete a league that has specified id  |
|/api/stadiums                 | GET        |                            | Get all stadiums                       |
|/api/stadiums/{stadiumId}     | GET        |                            | Get a stadium that has specified id    |
|/api/stadiums                 | POST       | Stadium JSON               | Create a new stadium from json data    |
|/api/stadiums/{stadiumId}     | PUT        | Stadium JSON               | Replace a stadium with new stadium data|
|/api/stadiums/{stadiumId}     | DELETE     |                            | Delete a stadium that has specified id |
|/api/teams                    | GET        |                            | Get all teams                          |
|/api/teams?nameContains=      | GET        |                            | Filter teams by name                   |
|/api/teams/{teamId}           | GET        |                            | Get a team that has specified id       |
|/api/teams/{teamId}/matches   | GET        |                            | Get all matches of a team that has specified id |
|/api/teams                    | POST       | Team JSON                  | Create a new team from json data       |
|/api/teams/{teamId}           | PUT        | Team JSON                  | Replace a team with new team data      |
|/api/teams/{teamId}           | DELETE     |                            | Delete a team that has specified id    |
|/api/matches                  | GET        |                            | Get all matches                        |
|/api/matches?status=          | GET        |                            | Filter matches by status               |
|/api/matches?dateWithin=      | GET        |                            | Filter matches by date                 |
|/api/matches/{matchId}        | GET        |                            | Get a match that has specified id      |
|/api/matches                  | POST       | Match JSON                 | Create a new match from match data     |
|/api/matches/{matchId}        | PUT        | Match JSON                 | Replace a match with new match data    |
|/api/matches/{matchId}        | DELETE     |                            | Delete a match that has specified id   |
|/api/matches/{matchId}/events | GET        |                            | Get all events that occured in this match |
|/api/matches/{matchId}/events | POST       | Event JSON (of chosen type)| Create a new event that belongs to the match with specified id |


### Json Objects

#### League JSON

* name and country cannot be null
* name field length must be between 3 and 50 characters
* country field length must be between 2 and 30 characters

```JSON
{
  "name" : "League name",
  "country" : "League country"
}
```

#### Stadium JSON

* name and city cannot be null
* name field length must be between 3 and 200 characters
* city field length must be between 2 and 150 characters
* capacity field is not required
* capacity field when provided, must be a positive integer

```JSON
{
  "name" : "Stadium name",
  "city" : "Stadium city",
  "capacity" : 30000
}
```

#### Team JSON

* name and leagueId cannot be null
* name field length must be between 2 and 200 characters
* leagueId field must contain an ID that points to an existing league

```JSON
{
  "name" : "Team name",
  "leagueId" : 10
}
```

#### Match JSON

* teamA, teamB and startDate cannot be null
* teamA, teamB must contain IDs that point to existing teams
* teamA and teamB cannot be equal
* league and stadium are optional, but when provided, they must contain IDs that point to existing resources
* startDate format "YYYY-MM-DD HH:MM", other formats will result in failed parsing

```JSON
{
  "startDate" : "2018-12-01 20:00",
  "teamA" : 1,
  "teamB" : 5,
  "league" : 3,
  "stadium" : 7
}
``` 

#### Event JSON

##### ManagingEvent

Use: to change current match status.

* all fields are required
* time value must be between 1 and 120
* type value must be one of these: *START_FIRST_HALF*, *FINISH_FIRST_HALF*, *START_SECOND_HALF*, *FINISH_SECOND_HALF*, *FINISH_MATCH*, *START_OT_FIRST_HALF*, *FINISH_OT_FIRST_HALF*, *START_OT_SECOND_HALF*, *FINISH_OT_SECOND_HALF*

```JSON
{
  "time" : 1,
  "message" : "Event message contents",
  "type" : "START_FIRST_HALF"
}
```

##### StandardEvent

Use: to post short descriptions of the actions on the pitch that do not fall under categories such as: goal, substitution, card, penalty 

* all fields are required
* time value must be between 1 and 120
* type value must be exactly *STANDARD_DESCRIPTION*

```JSON
{
  "time" : 20,
  "message" : "Event message contents",
  "type" : "STANDARD_DESCRIPTION"
}
```

##### GoalEvent

Use: to inform about a goal. This event increases the goal count of the goalscoring team and saves the information about the goalscorer name

* all fields are required
* time value must be between 1 and 120
* type value must be exactly *GOAL*
* teamId value must be an Id of one of the teams that play in a match. Id of a team that does not exist or does not play in this match will be rejected

```JSON
{
  "time" : 25,
  "message" : "Goal scored!!!",
  "type" : "GOAL",
  "teamId" : 1,
  "scorerName" : "Test player"
}
```

##### SubstitutionEvent

Use: to inform about the substitution of a player.

* all fields are required
* time value must be between 1 and 120
* type value must be exactly *SUBSTITUTION*

```JSON
{
  "time" : 77,
  "message" : "Substitution on the pitch",
  "type" : "SUBSTITUTION",
  "playerIn": "Test player2",
  "playerOut" : "Test player1"
}
```

##### CardEvent

Use: to inform that YELLOW/RED card was given to a player

* all fields are required
* time value must be between 1 and 120
* type value must be exactly *CARD*
* color value must be exactly *YELLOW* or *RED*. Other values will be rejected

```JSON
{
  "time" : 55,
  "message" : "The referee gives a yellow card",
  "type" : "CARD",
  "player" : "Test player1",
  "color" : "YELLOW"
}
```

##### PenaltyEvent

Use: to inform that a penalty in a penalty shootout was scored. Information about regular penalties (during the match or in the overtime) 
should be sent as a GoalEvent. 
This event increments teamA/teamB penalty counters that are separate from the regular score goal counters. 
This makes it possible to keep scores such as ex. "2:2 (3:1)"

* all fields are required
* time value must be between 1 and 120 (penalty shootouts are after the OT, so by default the client can send 120)
* type value must be exactly *PENALTY*
* teamId value must be an Id of one of the teams that play in a match. Id of a team that does not exist or does not play in this match will be rejected

```JSON
{
 "time" : 120,
 "message" : "Shot in the middle",
 "type" : "PENALTY",
 "teamId" : 2
}
```