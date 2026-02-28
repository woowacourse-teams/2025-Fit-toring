package fittoring.config.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ExpiredTokenException;
import fittoring.config.auth.LoginInfo;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

class InboundChannelInterceptorTest {

    private WebSocketMetricsListener metricsListener;
    private InboundChannelInterceptor interceptor;
    private MessageChannel channel;

    @BeforeEach
    void setUp() {
        metricsListener = mock(WebSocketMetricsListener.class);
        interceptor = new InboundChannelInterceptor(metricsListener);
        channel = mock(MessageChannel.class);
    }

    @DisplayName("SEND 명령에서 토큰이 유효하면 메시지를 통과시킨다.")
    @Test
    void preSend_passesMessage_whenTokenNotExpired() {
        // given
        LoginInfo loginInfo = new LoginInfo(1L);
        Message<byte[]> message = buildMessage(StompCommand.SEND, loginInfo,
                Instant.now().plusSeconds(30).toEpochMilli());

        // when
        Message<?> result = interceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getHeaders().get(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY)).isEqualTo(loginInfo);
        verify(metricsListener).incrementInboundMessage(2);
    }

    @DisplayName("SEND 명령에서 토큰이 만료되면 예외를 던진다.")
    @Test
    void preSend_throwsException_whenSendTokenExpired() {
        // given
        Message<byte[]> message = buildMessage(
                StompCommand.SEND,
                new LoginInfo(1L),
                Instant.now().minusMillis(1).toEpochMilli()
        );

        // when & then
        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessage(BusinessErrorMessage.EXPIRED_TOKEN.getMessage());
        verifyNoInteractions(metricsListener);
    }

    @DisplayName("SUBSCRIBE 명령에서 토큰이 만료되면 예외를 던진다.")
    @Test
    void preSend_throwsException_whenSubscribeTokenExpired() {
        // given
        Message<byte[]> message = buildMessage(
                StompCommand.SUBSCRIBE,
                new LoginInfo(1L),
                Instant.now().minusMillis(1).toEpochMilli()
        );

        // when & then
        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessage(BusinessErrorMessage.EXPIRED_TOKEN.getMessage());
        verifyNoInteractions(metricsListener);
    }

    private Message<byte[]> buildMessage(StompCommand command, LoginInfo loginInfo, long expMillis) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setLeaveMutable(true);
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY, loginInfo);
        sessionAttributes.put(WebSocketAuthHandshakeInterceptor.TOKEN_EXP_EPOCH_MILLIS_KEY, expMillis);
        accessor.setSessionAttributes(sessionAttributes);
        return MessageBuilder.createMessage("{}".getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
