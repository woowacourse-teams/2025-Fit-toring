package fittoring.application.exception;

public class UnauthorizedChatMessageAccessException extends RuntimeException {

    public UnauthorizedChatMessageAccessException(String message) {
        super(message);
    }
}
