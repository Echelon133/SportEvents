package ml.echelon133.sportevents.event.types.dto.validation;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EventTypeValidator implements ConstraintValidator<ValidEventType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            AbstractMatchEvent.EventType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            // If our event has a 'type' that is not in EventType enum, validation is failed
            return false;
        }
        return true;
    }

    @Override
    public void initialize(ValidEventType constraintAnnotation) {}
}
