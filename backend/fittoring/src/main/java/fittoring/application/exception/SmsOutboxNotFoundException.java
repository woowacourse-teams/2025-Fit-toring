package fittoring.application.exception;

public class SmsOutboxNotFoundException extends RuntimeException {

    public SmsOutboxNotFoundException(String message) {
        super(message);
    }
}
