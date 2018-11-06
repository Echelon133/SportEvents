package ml.echelon133.sportevents.event.types.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = CardColorValidator.class)
public @interface ValidCardColor {
    String message() default "Invalid card color";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}