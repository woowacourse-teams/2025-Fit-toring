package fittoring.application.infra.exception;

public class SmsException extends RuntimeException {

    public SmsException(String message) {
        super(message);
    }
}
