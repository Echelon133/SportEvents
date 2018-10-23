package ml.echelon133.sportevents.match.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = TeamIdsNotEqualValidator.class)
public @interface TeamIdsNotEqual {
    String message() default "Team IDs cannot be equal";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
