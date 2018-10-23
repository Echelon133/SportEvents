package ml.echelon133.sportevents.match.validation;

import ml.echelon133.sportevents.match.MatchDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TeamIdsNotEqualValidator implements ConstraintValidator<TeamIdsNotEqual, MatchDto> {

    public TeamIdsNotEqualValidator() {
    }

    @Override
    public boolean isValid(MatchDto value, ConstraintValidatorContext context) {
        return !value.getTeamA().equals(value.getTeamB());
    }

    @Override
    public void initialize(TeamIdsNotEqual constraintAnnotation) {}
}
