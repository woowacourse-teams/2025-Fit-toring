package fittoring.config.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.chat.service.ChatRoomService;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ExpiredTokenException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.application.exception.UnauthorizedException;
import fittoring.config.auth.LoginInfo;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

class InboundChannelInterceptorTest {

    private JwtProvider jwtProvider;
    private ChatRoomService chatRoomService;
    private WebSocketMetricsListener metricsListener;
    private InboundChannelInterceptor interceptor;
    private MessageChannel channel;

    @BeforeEach
    void setUp() {
        jwtProvider = mock(JwtProvider.class);
        chatRoomService = mock(ChatRoomService.class);
        metricsListener = mock(WebSocketMetricsListener.class);
        interceptor = new InboundChannelInterceptor(jwtProvider, chatRoomService, metricsListener);
        channel = mock(MessageChannel.class);
    }

    @DisplayName("CONNECT 명령에서 토큰이 유효하면 인증 정보를 세션에 저장한다.")
    @Test
    void preSend_authenticatesSession_whenConnectTokenValid() {
        // given
        Message<byte[]> message = buildConnectMessageWithSessionToken("token-value");
        when(jwtProvider.extractTokenPayload("token-value"))
                .thenReturn(new TokenPayload(1L, "MENTEE"));
        when(jwtProvider.extractExpirationMillis("token-value"))
                .thenReturn(1_800_000_000_000L);

        // when
        Message<?> result = interceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getHeaders()
                .get("simpSessionAttributes", Map.class)
                .get("loginInfo"))
                .isEqualTo(new LoginInfo(1L));
        assertThat(result.getHeaders()
                .get("simpSessionAttributes", Map.class)
                .get("tokenExpEpochMillis"))
                .isEqualTo(1_800_000_000_000L);
        verifyNoInteractions(metricsListener);
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
        assertThat(result.getHeaders().get("loginInfo")).isEqualTo(loginInfo);
        verify(metricsListener).incrementInboundMessage(2);
        verifyNoInteractions(chatRoomService);
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

    @DisplayName("채팅방 SUBSCRIBE 시 접근 권한이 검증되면 세션에 채팅방 권한을 저장한다.")
    @Test
    void preSend_cachesAuthorizedChatRoom_whenSubscribeChatRoom() {
        // given
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("loginInfo", new LoginInfo(1L));
        sessionAttributes.put("tokenExpEpochMillis", Instant.now().plusSeconds(30).toEpochMilli());
        Message<byte[]> message = buildMessage(
                StompCommand.SUBSCRIBE,
                "/topic/chatroom/123",
                sessionAttributes
        );

        // when
        Message<?> result = interceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        assertThat(sessionAttributes.get("authorizedChatRoomIds")).isEqualTo(new HashSet<>(Set.of(123L)));
        verify(chatRoomService).getAccessibleChatRoom(123L, 1L);
        verifyNoInteractions(metricsListener);
    }

    @DisplayName("채팅방 구독 없이 SEND 하면 예외를 던진다.")
    @Test
    void preSend_throwsException_whenSendChatRoomNotAuthorized() {
        // given
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("loginInfo", new LoginInfo(1L));
        sessionAttributes.put("tokenExpEpochMillis", Instant.now().plusSeconds(30).toEpochMilli());
        Message<byte[]> message = buildMessage(
                StompCommand.SEND,
                "/app/chatroom/123",
                sessionAttributes
        );

        // when & then
        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class)
                .hasMessage(BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage());
        verifyNoInteractions(metricsListener);
        verifyNoInteractions(chatRoomService);
    }

    @DisplayName("이미 권한이 캐시된 채팅방 SEND 는 DB 조회 없이 통과한다.")
    @Test
    void preSend_passesMessage_whenSendChatRoomAuthorized() {
        // given
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("loginInfo", new LoginInfo(1L));
        sessionAttributes.put("tokenExpEpochMillis", Instant.now().plusSeconds(30).toEpochMilli());
        sessionAttributes.put("authorizedChatRoomIds", new HashSet<>(Set.of(123L)));
        Message<byte[]> message = buildMessage(
                StompCommand.SEND,
                "/app/chatroom/123",
                sessionAttributes
        );

        // when
        Message<?> result = interceptor.preSend(message, channel);

        // then
        assertThat(result).isNotNull();
        verify(metricsListener).incrementInboundMessage(2);
        verify(chatRoomService, never()).getAccessibleChatRoom(123L, 1L);
    }

    @DisplayName("인증되지 않은 세션의 SEND 명령은 예외를 던진다.")
    @Test
    void preSend_throwsException_whenSessionNotAuthenticated() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setLeaveMutable(true);
        accessor.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.createMessage(
                "{}".getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders()
        );

        // when & then
        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
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
        sessionAttributes.put("loginInfo", loginInfo);
        sessionAttributes.put("tokenExpEpochMillis", expMillis);
        accessor.setSessionAttributes(sessionAttributes);
        return MessageBuilder.createMessage("{}".getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

    private Message<byte[]> buildMessage(
            StompCommand command,
            String destination,
            Map<String, Object> sessionAttributes
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setLeaveMutable(true);
        accessor.setDestination(destination);
        accessor.setSessionAttributes(sessionAttributes);
        return MessageBuilder.createMessage("{}".getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }

    private Message<byte[]> buildConnectMessageWithSessionToken(String accessToken) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("accessToken", accessToken);
        accessor.setSessionAttributes(sessionAttributes);
        return MessageBuilder.createMessage("{}".getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
