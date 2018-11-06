package ml.echelon133.sportevents.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ml.echelon133.sportevents.event.types.dto.*;


/*
This mix-in maps every event 'type' value to a target class that it should be deserialized into

Every event that starts/stops/ends a match should be deserialized into ManagingEventDto
Event that carries information about a goal scored should be deserialized into GoalEventDto
Event that contains information about a card given to a player should be deserialized into CardEventDto
Event that has description of some situation on the pitch that does not fall under other event types should be
    deserialized into StandardEventDto
Event that informs about substitution of a player should be deserialized into SubstitutionEventDto
Event that contains information about a penalty in a penalty shootout should be deserialized into PenaltyEventDto
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GoalEventDto.class, name = "GOAL"),
        @JsonSubTypes.Type(value = CardEventDto.class, name = "CARD"),
        @JsonSubTypes.Type(value = SubstitutionEventDto.class, name = "SUBSTITUTION"),
        @JsonSubTypes.Type(value = StandardEventDto.class, name = "STANDARD_DESCRIPTION"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "START_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "FINISH_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "START_SECOND_HALF"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "FINISH_SECOND_HALF"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "FINISH_MATCH"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "START_OT_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "FINISH_OT_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "START_OT_SECOND_HALF"),
        @JsonSubTypes.Type(value = ManagingEventDto.class, name = "FINISH_OT_SECOND_HALF"),
        @JsonSubTypes.Type(value = PenaltyEventDto.class, name = "PENALTY")
})
public abstract class EventMixIn {
}
