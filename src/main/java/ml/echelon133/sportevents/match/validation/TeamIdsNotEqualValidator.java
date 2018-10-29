package ml.echelon133.sportevents.match.validation;

import ml.echelon133.sportevents.match.MatchDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TeamIdsNotEqualValidator implements ConstraintValidator<TeamIdsNotEqual, MatchDto> {

    public TeamIdsNotEqualValidator() {
    }

    @Override
    public boolean isValid(MatchDto value, ConstraintValidatorContext context) {
        boolean isValid = false;
        try {
            isValid = !value.getTeamA().equals(value.getTeamB());
        } catch (NullPointerException ex) {
            isValid = false;
        } finally {
            return isValid;
        }

    }

    @Override
    public void initialize(TeamIdsNotEqual constraintAnnotation) {}
}
