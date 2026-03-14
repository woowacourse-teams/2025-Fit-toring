package fittoring.config.websocket;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ExpiredTokenException;
import fittoring.application.exception.InvalidTokenException;
import fittoring.logging.ErrorJsonLogger;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MimeTypeUtils;

class ChatStompErrorHandlerTest {

    private ObjectMapper objectMapper;
    private ChatStompErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        errorHandler = new ChatStompErrorHandler(objectMapper, new ErrorJsonLogger(objectMapper));
    }

    @DisplayName("토큰 만료 예외가 발생하면 TOKEN_EXPIRED 코드의 ERROR 프레임을 반환한다.")
    @Test
    void handleClientMessageProcessingError_returnsTokenExpiredErrorFrame() throws Exception {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setDestination("/app/chatroom/42");
        accessor.setReceipt("receipt-1");
        Message<byte[]> clientMessage = MessageBuilder.createMessage(
                "payload".getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders()
        );

        Throwable ex = new RuntimeException(
                new ExpiredTokenException(BusinessErrorMessage.EXPIRED_TOKEN.getMessage())
        );

        // when
        Message<byte[]> errorMessage = errorHandler.handleClientMessageProcessingError(clientMessage, ex);

        // then
        StompHeaderAccessor errorAccessor = MessageHeaderAccessor.getAccessor(errorMessage, StompHeaderAccessor.class);
        assertThat(errorAccessor).isNotNull();
        assertThat(errorAccessor.getCommand()).isEqualTo(StompCommand.ERROR);
        assertThat(errorAccessor.getContentType()).isEqualTo(MimeTypeUtils.APPLICATION_JSON);
        assertThat(errorAccessor.getReceiptId()).isEqualTo("receipt-1");

        JsonNode payload = objectMapper.readTree(errorMessage.getPayload());
        assertThat(payload.get("statusCode").asInt()).isEqualTo(401);
        assertThat(payload.get("code").asText()).isEqualTo("TOKEN_EXPIRED");
        assertThat(payload.get("message").asText()).isEqualTo(BusinessErrorMessage.EXPIRED_TOKEN.getMessage());
        assertThat(payload.get("chatRoomId").asLong()).isEqualTo(42L);
        assertThat(payload.hasNonNull("traceId")).isTrue();
        assertThat(payload.hasNonNull("timestamp")).isTrue();
    }

    @DisplayName("CONNECT 단계의 잘못된 토큰 예외도 ERROR 프레임으로 반환한다.")
    @Test
    void handleClientMessageProcessingError_returnsInvalidTokenErrorFrame() throws Exception {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Message<byte[]> clientMessage = MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );

        Throwable ex = new RuntimeException(
                new InvalidTokenException(BusinessErrorMessage.INVALID_TOKEN.getMessage())
        );

        // when
        Message<byte[]> errorMessage = errorHandler.handleClientMessageProcessingError(clientMessage, ex);

        // then
        StompHeaderAccessor errorAccessor = MessageHeaderAccessor.getAccessor(errorMessage, StompHeaderAccessor.class);
        assertThat(errorAccessor).isNotNull();
        assertThat(errorAccessor.getCommand()).isEqualTo(StompCommand.ERROR);
        assertThat(errorAccessor.getMessage()).isEqualTo(BusinessErrorMessage.INVALID_TOKEN.getMessage());

        JsonNode payload = objectMapper.readTree(errorMessage.getPayload());
        assertThat(payload.get("statusCode").asInt()).isEqualTo(401);
        assertThat(payload.get("code").asText()).isEqualTo("INVALID_TOKEN");
        assertThat(payload.get("message").asText()).isEqualTo(BusinessErrorMessage.INVALID_TOKEN.getMessage());
    }

    @DisplayName("일반 예외는 기본 STOMP ERROR 처리로 위임한다.")
    @Test
    void handleClientMessageProcessingError_delegatesToDefaultHandler() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        Message<byte[]> clientMessage = MessageBuilder.createMessage(
                "payload".getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders()
        );

        // when
        Message<byte[]> errorMessage = errorHandler.handleClientMessageProcessingError(
                clientMessage,
                new IllegalArgumentException("bad request")
        );

        // then
        StompHeaderAccessor errorAccessor = MessageHeaderAccessor.getAccessor(errorMessage, StompHeaderAccessor.class);
        assertThat(errorAccessor).isNotNull();
        assertThat(errorAccessor.getCommand()).isEqualTo(StompCommand.ERROR);
        assertThat(errorAccessor.getMessage()).isEqualTo("bad request");
        assertThat(errorMessage.getPayload()).isEmpty();
    }
}
