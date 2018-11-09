package ml.echelon133.sportevents.event;

public class ProcessedEventRejectedException extends Exception {

    public ProcessedEventRejectedException() {
    }

    public ProcessedEventRejectedException(String message) {
        super(message);
    }
}
