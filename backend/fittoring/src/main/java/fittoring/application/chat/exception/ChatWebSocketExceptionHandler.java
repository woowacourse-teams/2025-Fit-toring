package fittoring.application.chat.exception;

import fittoring.application.chat.presentation.dto.response.ChatWebSocketErrorResponse;
import fittoring.application.exception.ChatMessageNotFoundException;
import fittoring.application.exception.ChatMessageNotImageException;
import fittoring.application.exception.ChatRoomNotFoundException;
import fittoring.application.exception.UnauthorizedChatMessageAccessException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.exception.SystemErrorMessage;
import fittoring.logging.ErrorJsonLogger;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@RequiredArgsConstructor
@ControllerAdvice
public class ChatWebSocketExceptionHandler {

    private static final Pattern CHAT_ROOM_ID_PATTERN = Pattern.compile("/chatroom/(\\d+)");
    private static final String ERROR_PATH = "/queue/errors";

    private final ErrorJsonLogger errorJsonLogger;

    @MessageExceptionHandler({
            ChatRoomNotFoundException.class,
            ChatMessageNotFoundException.class
    })
    @SendToUser(destinations = ERROR_PATH, broadcast = false)
    public ChatWebSocketErrorResponse handleNotFound(Exception e, Message<?> message) {
        return buildResponse(e, message, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @MessageExceptionHandler({
            UnauthorizedChatRoomAccessException.class,
            UnauthorizedChatMessageAccessException.class
    })
    @SendToUser(destinations = ERROR_PATH, broadcast = false)
    public ChatWebSocketErrorResponse handleForbidden(Exception e, Message<?> message) {
        return buildResponse(e, message, HttpStatus.FORBIDDEN, e.getMessage());
    }

    @MessageExceptionHandler(ChatMessageNotImageException.class)
    @SendToUser(destinations = ERROR_PATH, broadcast = false)
    public ChatWebSocketErrorResponse handleBadRequest(ChatMessageNotImageException e, Message<?> message) {
        return buildResponse(e, message, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser(destinations = ERROR_PATH, broadcast = false)
    public ChatWebSocketErrorResponse handleValidation(MethodArgumentNotValidException e, Message<?> message) {
        String messageText = extractValidationMessage(e);

        return buildResponse(e, message, HttpStatus.BAD_REQUEST, messageText, messageText);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser(destinations = ERROR_PATH, broadcast = false)
    public ChatWebSocketErrorResponse handleUnhandled(Exception e, Message<?> message) {
        return buildResponse(
                e,
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                SystemErrorMessage.INTERNAL_SERVER_ERROR.getMessage()
        );
    }

    private ChatWebSocketErrorResponse buildResponse(
            Throwable e,
            Message<?> message,
            HttpStatus status,
            String responseMessage
    ) {
        return buildResponse(e, message, status, responseMessage, e.getMessage());
    }

    private ChatWebSocketErrorResponse buildResponse(
            Throwable e,
            Message<?> message,
            HttpStatus status,
            String responseMessage,
            String logMessage
    ) {
        String destination = resolveDestination(message);
        String traceId = java.util.UUID.randomUUID().toString();
        Long chatRoomId = extractChatRoomId(destination);

        errorJsonLogger.logWithContext(
                e,
                status,
                "WS_SEND",
                destination,
                destination,
                null,
                traceId,
                logMessage
        );

        return ChatWebSocketErrorResponse.of(
                status.value(),
                e.getClass().getSimpleName(),
                responseMessage,
                traceId,
                chatRoomId
        );
    }

    private String extractValidationMessage(MethodArgumentNotValidException e) {
        return Objects.requireNonNull(e.getBindingResult())
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");
    }

    private String resolveDestination(Message<?> message) {
        if (message == null) {
            return "unknown";
        }
        String destination = getDestination(message);
        if (destination == null || destination.isBlank()) {
            return "unknown";
        }
        return destination;
    }

    @Nullable
    private String getDestination(Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        return accessor.getDestination();
    }

    private Long extractChatRoomId(String destination) {
        Matcher matcher = CHAT_ROOM_ID_PATTERN.matcher(destination);
        if (!matcher.find()) {
            return null;
        }
        return Long.parseLong(matcher.group(1));
    }
}
