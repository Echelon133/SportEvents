package ml.echelon133.sportevents.event.types.dto.validation;

import ml.echelon133.sportevents.event.types.CardEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CardColorValidator implements ConstraintValidator<ValidCardColor, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = false;
        try {
            if (value != null) {
                CardEvent.CardColor.valueOf(value);
                isValid = true;
            } else {
                isValid = false;
            }
        } catch (IllegalArgumentException ex) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void initialize(ValidCardColor constraintAnnotation) {}
}
