package ml.echelon133.sportevents.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    static class ErrorMessage {
        private Date timestamp;
        private List<String> messages;
        private String path;

        ErrorMessage(Date timestamp, String path, String... messages) {
            this.timestamp = timestamp;
            this.path = path;
            this.messages = Arrays.asList(messages);
        }

        ErrorMessage(Date timestamp, String path, List<String> messages) {
            this.timestamp = timestamp;
            this.path = path;
            this.messages = messages;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public List<String> getMessages() {
            return messages;
        }

        public String getPath() {
            return path;
        }
    }

    @ExceptionHandler(value = ResourceDoesNotExistException.class)
    protected ResponseEntity<ErrorMessage> handleResourceDoesNotExistException(ResourceDoesNotExistException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), request.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = FailedValidationException.class)
    protected ResponseEntity<ErrorMessage> handleFailedValidationException(FailedValidationException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), request.getDescription(false), ex.getTextErrors());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ParseException.class)
    protected ResponseEntity<ErrorMessage> handleParseException(ParseException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(new Date(), request.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
