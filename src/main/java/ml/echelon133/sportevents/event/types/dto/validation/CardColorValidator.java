package ml.echelon133.sportevents.event.types.dto.validation;

import ml.echelon133.sportevents.event.types.CardEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CardColorValidator implements ConstraintValidator<ValidCardColor, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            CardEvent.CardColor.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void initialize(ValidCardColor constraintAnnotation) {}
}
