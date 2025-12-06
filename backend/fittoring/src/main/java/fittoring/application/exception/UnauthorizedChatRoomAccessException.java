package fittoring.application.exception;

public class UnauthorizedChatRoomAccessException extends RuntimeException {

    public UnauthorizedChatRoomAccessException(String message) {
        super(message);
    }
}
