# SportEvents Application

REST API for live text coverage of football matches. Each match has a separate websocket topic. Thus, it is possible to
read all past events of a certain match through a GET request and then to receive all future events live over a 
websocket connection.


Application features:

* OAuth2 authentication system (tokens provided by github)
* Validation of user-provided data
* CRUD operations on all resources (stadiums, teams, matches, leagues)
* Live listening to match events thanks to websockets 

# Documentation

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
|/api/matches?status=          | GET        |                            | Filter matches by status [1]           |
|/api/matches?dateWithin=      | GET        |                            | Filter matches by date within [2]      |
|/api/matches/{matchId}        | GET        |                            | Get a match that has specified id      |
|/api/matches                  | POST       | Match JSON                 | Create a new match from match data     |
|/api/matches/{matchId}        | PUT        | Match JSON                 | Replace a match with new match data    |
|/api/matches/{matchId}        | DELETE     |                            | Delete a match that has specified id   |
|/api/matches/{matchId}/events | GET        |                            | Get all events that occured in this match |
|/api/matches/{matchId}/events | POST       | Event JSON (of chosen type)| Create a new event that belongs to the match with specified id |

[1] - accepted values: NOT_STARTED, FIRST_HALF, SECOND_HALF, FINISHED, OT_FIRST_HALF, OT_SECOND_HALF, PENALTIES, BREAK_TIME

[2] - accepted values: DAY, THREE_DAYS, WEEK

## Json Objects

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

## Events accepted depending on current match status

|event type \ match status | NOT_STARTED   | FIRST_HALF| BREAK_TIME| SECOND_HALF  | OT_FIRST_HALF | OT_SECOND_HALF   | PENALTIES | FINISHED|
|--------------------------|---------------|-----------|-----------|--------------|---------------|------------------|-----------|---------|
| STANDARD_DESCRIPTION     |  YES          | YES       |  YES      |  YES         |  YES          |   YES            | NO        | YES     |
| START_FIRST_HALF         |  YES          | NO        |  NO       |  NO          |  NO           |   NO             | NO        | NO      |
| FINISH_FIRST_HALF        |  NO           | YES       |  NO       |  NO          |  NO           |   NO             | NO        | NO      |
| START_SECOND_HALF        |  NO           | NO        |  YES      |  NO          |  NO           |   NO             | NO        | NO      |
| FINISH_SECOND_HALF       |  NO           | NO        |  NO       |  YES         |  NO           |   NO             | NO        | NO      |
| FINISH_MATCH             |  NO           | NO        |  NO       |  YES         |  NO           |   YES            | YES       | NO      |
| START_OT_FIRST_HALF      |  NO           | NO        |  YES      |  NO          |  NO           |   NO             | NO        | NO      |
| FINISH_OT_FIRST_HALF     |  NO           | NO        |  NO       |  NO          |  YES          |   NO             | NO        | NO      |
| START_OT_SECOND_HALF     |  NO           | NO        |  YES      |  NO          |  NO           |   NO             | NO        | NO      |
| FINISH_OT_SECOND_HALF    |  NO           | NO        |  NO       |  NO          |  NO           |   YES            | NO        | NO      |
| GOAL                     |  NO           | YES       |  NO       |  YES         |  YES          |   YES            | NO        | NO      |
| CARD                     |  NO           | YES       |  NO       |  YES         |  YES          |   YES            | NO        | NO      |
| SUBSTITUTION             |  NO           | YES       |  YES      |  YES         |  YES          |   YES            | NO        | NO      |
| PENALTY                  |  NO           | NO        |  NO       |  NO          |  NO           |   NO             | YES       | NO      |
 

## Websockets

To be able to listen to match events over a STOMP endpoint, the client has to connect to **/sport-events**.
There is no need to authenticate when connecting to this endpoint. The application does not accept any messages sent in.
Every client can connect to this endpoint, regardless its origin (CORS set to allow all).

Example code of a client app, that uses SockJS and Stomp:
```JS
var socket = new SockJS("http://localhost:8080/sport-events");
var stompClient = Stomp.over(socket);

stompClient.connect({}, onConnection, onError);
``` 

When an event is added to a particular match, it is simultaneously sent over to a topic **/matches/{matchId}**, where
*matchId* is the ID of a specific match. 

Every match resource has a *websocketPath* field that contains full topic name where this match events will be accessible.

```JS
var websocketPath = matchJSON.websocketPath;
stompClient.subscribe(websocketPath, onMessageReceived);
```

## Screens

### OAuth2 token generation

![GET_ACCESS_TOKEN](https://github.com/Echelon133/SportEvents/blob/docs/screens/Oauth2/1GET_ACCESS_TOKEN.png)
![SAVE_TOKEN](https://github.com/Echelon133/SportEvents/blob/docs/screens/Oauth2/2SAVE_ACCESS_TOKEN.png)

### Example use of /api/stadiums

![GET_EMPTY](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/1GET_EMPTY_STADIUM_RESOURCE.png)
![CREATE_STADIUM](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/2CREATE_STADIUM_RESOURCE.png)
![GET_RESOURCE](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/3GET_STADIUM_RESOURCE.png)
![GET_404](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/4GET_NON_EXISTENT_STADIUM.png)
![PUT_RESOURCE](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/5REPLACE_STADIUM_RESOURCE.png)
![GET_RESOURCES](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/6GET_ALL_STADIUM_RESOURCES.png)
![DELETE_RESOURCE](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/7DELETE_STADIUM_RESOURCE.png)
![GET_AFTER_DELETION](https://github.com/Echelon133/SportEvents/blob/docs/screens/Stadium/8GET_ALL_STADIUM_RESOURCES_AFTER_DELETION.png)

### Example use of /api/leagues

![GET_EMPTY](https://github.com/Echelon133/SportEvents/blob/docs/screens/League/1GET_EMPTY_LEAGUE_RESOURCE.png)
![GET_404](https://github.com/Echelon133/SportEvents/blob/docs/screens/League/2GET_NON_EXISTENT_LEAGUE_RESOURCE.png)
![CREATE_LEAGUE](https://github.com/Echelon133/SportEvents/blob/docs/screens/League/3CREATE_LEAGUE_RESOURCE.png)
![GET_RESOURCES](https://github.com/Echelon133/SportEvents/blob/docs/screens/League/4GET_ALL_LEAGUE_RESOURCES.png)
![DELETE_RESOURCE](https://github.com/Echelon133/SportEvents/blob/docs/screens/League/5DELETE_LEAGUE_RESOURCE.png)
![GET_AFTER_DELETION](https://github.com/Echelon133/SportEvents/blob/docs/screens/League/6GET_ALL_LEAGUE_RESOURCES.png)
##### When creating a team that belongs to a specific league, the league receives a reference to the new team. With the request shown below we can list all teams that belong to a certain league.
![GET_LEAGUE_TEAMS](https://github.com/Echelon133/SportEvents/blob/docs/screens/League/7GET_LEAGUE_TEAMS.png)

### Example use of /api/teams

![GET_EMPTY](https://github.com/Echelon133/SportEvents/blob/docs/screens/Team/1GET_EMPTY_TEAM_RESOURCE.png)
![GET_404](https://github.com/Echelon133/SportEvents/blob/docs/screens/Team/2GET_NON_EXISTENT_TEAM_RESOURCE.png)
![CREATE_TEAM](https://github.com/Echelon133/SportEvents/blob/docs/screens/Team/3CREATE_TEAM.png)
![GET_RESOURCES](https://github.com/Echelon133/SportEvents/blob/docs/screens/Team/4GET_ALL_TEAM_RESOURCES.png)
![DELETE_RESOURCE](https://github.com/Echelon133/SportEvents/blob/docs/screens/Team/5DELETE_TEAM_RESOURCE.png)
![FILTER_RESOURCES](https://github.com/Echelon133/SportEvents/blob/docs/screens/Team/6FILTER_TEAM_BY_NAME_CONTAINS.png)

### Example use of /api/matches

![GET_EMPTY](https://github.com/Echelon133/SportEvents/blob/docs/screens/Match/1GET_EMPTY_MATCHES.png)
![GET_404](https://github.com/Echelon133/SportEvents/blob/docs/screens/Match/2GET_404.png)
![CREATE_MATCH](https://github.com/Echelon133/SportEvents/blob/docs/screens/Match/3CREATE_MATCH_RESOURCE.png)
![MATCH_VALIDATION](https://github.com/Echelon133/SportEvents/blob/docs/screens/Match/4MATCH_TEAM_ID_VALIDATION.png)
![REPLACE_MATCH](https://github.com/Echelon133/SportEvents/blob/docs/screens/Match/5REPLACE_MATCH_RESOURCE.png)
![FILTER_BY_STATUS](https://github.com/Echelon133/SportEvents/blob/docs/screens/Match/6GET_FILTERED_RESOURCES.png)
![DELETE_RESOURCE](https://github.com/Echelon133/SportEvents/blob/docs/screens/Match/7DELETE_MATCH_RESOURCE.png)

