package fittoring.application.chat.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.presentation.dto.response.ChatWebSocketErrorResponse;
import fittoring.application.exception.ChatMessageNotFoundException;
import fittoring.application.exception.ChatMessageNotImageException;
import fittoring.application.exception.ChatRoomNotFoundException;
import fittoring.application.exception.UnauthorizedChatMessageAccessException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.exception.SystemErrorMessage;
import fittoring.logging.ErrorJsonLogger;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
    private static final String UNKNOWN_DESTINATION = "unknown";
    private static final String TEMP_ID_FIELD = "tempId";

    private final ObjectMapper objectMapper;
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
        String traceId = UUID.randomUUID().toString();
        Long chatRoomId = extractChatRoomId(destination);
        Long tempId = resolveTempId(message);

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
                chatRoomId,
                tempId
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
            return UNKNOWN_DESTINATION;
        }
        String destination = getDestination(message);
        if (destination == null || destination.isBlank()) {
            return UNKNOWN_DESTINATION;
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

    @Nullable
    private Long resolveTempId(Message<?> message) {
        Object payload = message == null ? null : message.getPayload();
        if (payload == null) {
            return null;
        }
        if (payload instanceof ChatMessageRequest request) {
            return request.tempId();
        }
        if (payload instanceof Map<?, ?> payloadMap) {
            return resolveTempIdFromMap(payloadMap);
        }
        if (payload instanceof byte[] payloadBytes) {
            return resolveTempIdFromJsonBytes(payloadBytes);
        }
        return null;
    }

    @Nullable
    private Long resolveTempIdFromMap(Map<?, ?> payloadMap) {
        return toLong(payloadMap.get(TEMP_ID_FIELD));
    }

    @Nullable
    private Long resolveTempIdFromJsonBytes(byte[] payloadBytes) {
        try {
            JsonNode node = objectMapper.readTree(payloadBytes);
            if (!node.has(TEMP_ID_FIELD) || node.get(TEMP_ID_FIELD).isNull()) {
                return null;
            }
            return node.get(TEMP_ID_FIELD).asLong();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
