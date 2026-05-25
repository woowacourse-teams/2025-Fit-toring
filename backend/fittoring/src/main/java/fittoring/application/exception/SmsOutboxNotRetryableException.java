package fittoring.application.exception;

public class SmsOutboxNotRetryableException extends RuntimeException {

    public SmsOutboxNotRetryableException(String message) {
        super(message);
    }
}
