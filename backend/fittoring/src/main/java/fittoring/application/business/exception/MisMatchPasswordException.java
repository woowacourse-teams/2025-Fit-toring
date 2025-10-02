package fittoring.application.business.exception;

public class MisMatchPasswordException extends RuntimeException {

    public MisMatchPasswordException(String message) {
        super(message);
    }
}
