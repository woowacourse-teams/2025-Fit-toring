package fittoring.config.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.UnauthorizedException;
import fittoring.config.auth.LoginInfo;
import fittoring.logging.ErrorJsonLogger;
import jakarta.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.socket.WebSocketHandler;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthHandshakeInterceptorTest {

    private JwtProvider jwtProvider;
    private JwtExtractor jwtExtractor;
    private ObjectMapper objectMapper;
    private WebSocketAuthHandshakeInterceptor interceptor;
    private ErrorJsonLogger errorJsonLogger;

    @BeforeEach
    void setUp() {
        jwtProvider = mock(JwtProvider.class);
        jwtExtractor = mock(JwtExtractor.class);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        errorJsonLogger = new ErrorJsonLogger(objectMapper);
        interceptor = new WebSocketAuthHandshakeInterceptor(jwtProvider, jwtExtractor, objectMapper, errorJsonLogger);
    }

    @DisplayName("쿠키의 유효한 토큰이 있으면 로그인 정보를 세션 속성에 저장한다.")
    @Test
    void beforeHandshake_storesLoginInfo_whenCookieTokenValid() {
        // given
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        Cookie accessTokenCookie = new Cookie("accessToken", "token-value");
        servletRequest.setCookies(accessTokenCookie);

        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        when(jwtExtractor.extractTokenFromCookie("accessToken", servletRequest.getCookies()))
                .thenReturn("token-value");
        when(jwtProvider.extractTokenPayload("token-value"))
                .thenReturn(new TokenPayload(1L, "MENTEE"));

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes.get(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY))
                .isEqualTo(new LoginInfo(1L));
    }

    @DisplayName("쿠키가 없으면 인증 실패 응답을 반환한다.")
    @Test
    void beforeHandshake_throwsException_whenCookiesMissing() throws UnsupportedEncodingException {
        // given
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ServerHttpResponse response = new ServletServerHttpResponse(servletResponse);
        WebSocketHandler wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isFalse();
        assertThat(servletResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(servletResponse.getContentAsString())
                .contains("\"status\":\"UNAUTHORIZED\"")
                .contains("\"message\":\"" + BusinessErrorMessage.EMPTY_COOKIE.getMessage() + "\"")
                .contains("\"timestamp\"");
    }

    @DisplayName("인증 실패 시 예외 로그가 ErrorLog 포맷으로 기록된다.")
    @Test
    void beforeHandshake_logsErrorLogFormat_whenUnauthorized() throws Exception {
        // given
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setMethod("GET");
        servletRequest.setRequestURI("/ws/chat");
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ServerHttpResponse response = new ServletServerHttpResponse(servletResponse);
        WebSocketHandler wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        Logger logger = (Logger) LoggerFactory.getLogger(ErrorJsonLogger.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isFalse();

        ILoggingEvent errorEvent = listAppender.list.stream()
                .filter(event -> event.getLevel() == Level.WARN)
                .findFirst()
                .orElseThrow();

        JsonNode json = objectMapper.readTree(errorEvent.getFormattedMessage());
        assertThat(json.get("event").asText()).isEqualTo("ERROR");
        assertThat(json.get("method").asText()).isEqualTo("GET");
        assertThat(json.get("uri").asText()).isEqualTo("/ws/chat");
        assertThat(json.get("statusCode").asInt()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(json.get("errorType").asText()).isEqualTo(UnauthorizedException.class.getName());
        assertThat(json.get("message").asText()).isEqualTo(BusinessErrorMessage.EMPTY_COOKIE.getMessage());
        assertThat(json.get("stack").asText()).contains("WebSocketAuthHandshakeInterceptor");
        assertThat(json.get("normalizedUri").asText()).isEqualTo("/ws/chat");
        assertThat(json.hasNonNull("timestamp")).isTrue();
        assertThat(json.has("traceId")).isTrue();

        logger.detachAppender(listAppender);
    }

    @DisplayName("서블릿 요청이 아니면 인증을 생략하고 그대로 통과한다.")
    @Test
    void beforeHandshake_skipsAuth_whenNotServletRequest() {
        // given
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes).isEmpty();
        verifyNoInteractions(jwtExtractor, jwtProvider);
    }
}
