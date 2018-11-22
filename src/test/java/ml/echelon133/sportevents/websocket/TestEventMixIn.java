package ml.echelon133.sportevents.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ml.echelon133.sportevents.event.types.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StandardEvent.class, name = "STANDARD_DESCRIPTION"),
        @JsonSubTypes.Type(value = GoalEvent.class, name = "GOAL"),
        @JsonSubTypes.Type(value = CardEvent.class, name = "CARD"),
        @JsonSubTypes.Type(value = SubstitutionEvent.class, name = "SUBSTITUTION"),
        @JsonSubTypes.Type(value = StandardEvent.class, name = "STANDARD_DESCRIPTION"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "START_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "FINISH_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "START_SECOND_HALF"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "FINISH_SECOND_HALF"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "FINISH_MATCH"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "START_OT_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "FINISH_OT_FIRST_HALF"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "START_OT_SECOND_HALF"),
        @JsonSubTypes.Type(value = ManagingEvent.class, name = "FINISH_OT_SECOND_HALF"),
        @JsonSubTypes.Type(value = PenaltyEvent.class, name = "PENALTY")
})
public abstract class TestEventMixIn {
}
