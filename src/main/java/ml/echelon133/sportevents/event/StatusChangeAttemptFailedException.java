package ml.echelon133.sportevents.event;

public class StatusChangeAttemptFailedException extends Exception {

    public StatusChangeAttemptFailedException() {
    }

    public StatusChangeAttemptFailedException(String message) {
        super(message);
    }
}
