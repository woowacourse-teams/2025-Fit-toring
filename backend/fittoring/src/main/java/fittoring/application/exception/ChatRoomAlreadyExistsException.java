package fittoring.application.exception;

public class ChatRoomAlreadyExistsException extends RuntimeException {

    public ChatRoomAlreadyExistsException(String message) {
        super(message);
    }
}
