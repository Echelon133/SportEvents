package ml.echelon133.sportevents.exception;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class FailedValidationException extends Exception {

    private List<String> textErrors = new ArrayList<>();

    public FailedValidationException(String message) {
        super(message);
    }

    public FailedValidationException(List<FieldError> errors) {
        errors
                .forEach(e -> textErrors.add(e.getField() + " validation error: " + e.getDefaultMessage()));
    }

    public List<String> getTextErrors() {
        return textErrors;
    }
}
