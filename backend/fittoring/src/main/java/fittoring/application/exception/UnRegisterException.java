package fittoring.application.exception;

public class UnRegisterException extends RuntimeException {

    public UnRegisterException(String token) {
        super(token);
    }
}
