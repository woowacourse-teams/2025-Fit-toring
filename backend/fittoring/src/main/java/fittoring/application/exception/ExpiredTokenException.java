package fittoring.application.exception;

public class ExpiredTokenException extends AuthenticationException {

    public ExpiredTokenException(String message) {
        super(message);
    }
}
