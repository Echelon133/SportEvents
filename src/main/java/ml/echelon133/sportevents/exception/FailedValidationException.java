package ml.echelon133.sportevents.exception;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

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

    public FailedValidationException(List<FieldError> fErrors, List<ObjectError> oErrors) {
        this(fErrors);
        oErrors
                .forEach(e -> textErrors.add(e.getObjectName() + " validation error: " + e.getDefaultMessage()));
    }

    public List<String> getTextErrors() {
        return textErrors;
    }
}
