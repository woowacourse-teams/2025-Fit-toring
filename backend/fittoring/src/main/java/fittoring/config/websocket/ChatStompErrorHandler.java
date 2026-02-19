package fittoring.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.application.chat.presentation.dto.response.ChatWebSocketErrorResponse;
import fittoring.application.exception.ExpiredTokenException;
import fittoring.logging.ErrorJsonLogger;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@RequiredArgsConstructor
@Component
public class ChatStompErrorHandler extends StompSubProtocolErrorHandler {

    private static final String TOKEN_EXPIRED_CODE = "TOKEN_EXPIRED";
    private static final Pattern CHAT_ROOM_ID_PATTERN = Pattern.compile("/chatroom/(\\d+)");
    private static final StompCommand STOMP_COMMAND = StompCommand.ERROR;

    private final ObjectMapper objectMapper;
    private final ErrorJsonLogger errorJsonLogger;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
        Throwable cause = unwrapCause(ex);
        if (cause instanceof ExpiredTokenException expiredException) {
            return buildExpiredTokenErrorFrame(clientMessage, expiredException);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Throwable unwrapCause(Throwable ex) {
        Throwable cursor = ex;
        int depth = 0;
        while (cursor.getCause() != null && depth < 3) {
            cursor = cursor.getCause();
            depth++;
        }
        return cursor;
    }

    private Message<byte[]> buildExpiredTokenErrorFrame(
            @Nullable Message<byte[]> clientMessage,
            ExpiredTokenException exception
    ) {
        String traceId = UUID.randomUUID().toString();
        String destination = resolveDestination(clientMessage);
        String wsMethod = resolveWsMethod(clientMessage);
        Long chatRoomId = extractChatRoomId(destination);
        errorJsonLogger.logWithContext(
                exception,
                HttpStatus.UNAUTHORIZED,
                wsMethod,
                destination,
                destination,
                null,
                traceId
        );

        return createResponseMessage(clientMessage, exception, traceId, chatRoomId);
    }

    private String resolveDestination(@Nullable Message<byte[]> clientMessage) {
        if (clientMessage == null) {
            return "unknown";
        }
        MessageHeaders headers = clientMessage.getHeaders();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
        String destination = accessor != null ? accessor.getDestination() : null;
        if (destination == null || destination.isBlank()) {
            destination = (String) headers.get(StompHeaderAccessor.DESTINATION_HEADER);
        }
        if (destination == null || destination.isBlank()) {
            return "unknown";
        }
        return destination;
    }

    private String resolveWsMethod(@Nullable Message<byte[]> clientMessage) {
        if (clientMessage == null) {
            return "WS_UNKNOWN";
        }
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return "WS_UNKNOWN";
        }

        return switch (accessor.getCommand()) {
            case SEND -> "WS_SEND";
            case SUBSCRIBE -> "WS_SUBSCRIBE";
            default -> "WS_" + accessor.getCommand().name();
        };
    }

    @Nullable
    private Long extractChatRoomId(String destination) {
        Matcher matcher = CHAT_ROOM_ID_PATTERN.matcher(destination);
        if (!matcher.find()) {
            return null;
        }
        return Long.parseLong(matcher.group(1));
    }

    private Message<byte[]> createResponseMessage(
            @Nullable Message<byte[]> clientMessage,
            ExpiredTokenException exception,
            String traceId,
            Long chatRoomId
    ) {
        ChatWebSocketErrorResponse response = createErrorResponse(exception, traceId, chatRoomId);
        byte[] payload = toPayload(response);
        MessageHeaders messageHeaders = getStompMessageHeaders(clientMessage, exception);
        return MessageBuilder.createMessage(payload, messageHeaders);
    }

    private ChatWebSocketErrorResponse createErrorResponse(
            ExpiredTokenException exception,
            String traceId,
            Long chatRoomId
    ) {
        return ChatWebSocketErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                TOKEN_EXPIRED_CODE,
                exception.getMessage(),
                traceId,
                chatRoomId
        );
    }

    private byte[] toPayload(ChatWebSocketErrorResponse response) {
        try {
            return objectMapper.writeValueAsBytes(response);
        } catch (Exception ignored) {
            return new byte[0];
        }
    }

    private MessageHeaders getStompMessageHeaders(
            @Nullable Message<byte[]> clientMessage,
            ExpiredTokenException exception
    ) {
        StompHeaderAccessor accessor = createStompAccessor(clientMessage, exception);
        return accessor.getMessageHeaders();
    }

    private StompHeaderAccessor createStompAccessor(
            @Nullable Message<byte[]> clientMessage,
            ExpiredTokenException exception
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(STOMP_COMMAND);
        accessor.setMessage(exception.getMessage());
        accessor.setLeaveMutable(true);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        copyReceipt(clientMessage, accessor);
        return accessor;
    }

    private void copyReceipt(@Nullable Message<byte[]> clientMessage, StompHeaderAccessor accessor) {
        if (clientMessage == null) {
            return;
        }
        StompHeaderAccessor clientAccessor = MessageHeaderAccessor.getAccessor(clientMessage,
                StompHeaderAccessor.class);
        if (clientAccessor == null) {
            return;
        }
        String receiptId = clientAccessor.getReceipt();
        if (receiptId != null) {
            accessor.setReceiptId(receiptId);
        }
    }
}
