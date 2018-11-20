# SportEvents Application

## Documentation

| Endpoint                | Method     | Data sent with the request | Description                            |
|------------------------ |----------  |----------------------------|-------------                           |
|/api/leagues             | GET        |                            | Get all leagues                        |
|/api/leagues/{leagueId}  | GET        |                            | Get a league that has specified id     |
|/api/leagues/{leagueId}/teams  | GET        |                      | Get all teams of a league that has specified id |
|/api/leagues             | POST       | League JSON                | Create a new league from json data     |
|/api/leagues/{leagueId}  | PUT        | League JSON                | Replace a league with new league data  |
|/api/leagues/{leagueId}  | DELETE     |                            | Delete a league that has specified id  |
|/api/stadiums            | GET        |                            | Get all stadiums                       |
|/api/stadiums/{stadiumId}| GET        |                            | Get a stadium that has specified id    |
|/api/stadiums            | POST       | Stadium JSON               | Create a new stadium from json data    |
|/api/stadiums/{stadiumId}| PUT        | Stadium JSON               | Replace a stadium with new stadium data|
|/api/stadiums/{stadiumId}| DELETE     |                            | Delete a stadium that has specified id |
|/api/teams               | GET        |                            | Get all teams                          |
|/api/teams?nameContains= | GET        |                            | Filter teams by name                   |
|/api/teams/{teamId}      | GET        |                            | Get a team that has specified id       |
|/api/teams/{teamId}/matches      | GET        |                    | Get all matches of a team that has specified id |
|/api/teams               | POST       | Team JSON                  | Create a new team from json data       |
|/api/teams/{teamId}      | PUT        | Team JSON                  | Replace a team with new team data      |
|/api/teams/{teamId}      | DELETE     |                            | Delete a team that has specified id    |
|/api/matches             | GET        |                            | Get all matches                        |
|/api/matches?status=     | GET        |                            | Filter matches by status               |
|/api/matches?dateWithin= | GET        |                            | Filter matches by date                 |
|/api/matches/{matchId}   | GET        |                            | Get a match that has specified id      |
|/api/matches             | POST       | Match JSON                 | Create a new match from match data     |
|/api/matches/{matchId}   | PUT        | Match JSON                 | Replace a match with new match data    |
|/api/matches/{matchId}   | DELETE     |                            | Delete a match that has specified id   |


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


