package fittoring.config.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.socket.WebSocketHandler;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthHandshakeInterceptorTest {

    private WebSocketAuthHandshakeInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new WebSocketAuthHandshakeInterceptor();
    }

    @DisplayName("핸드셰이크에서 accessToken 쿠키가 있으면 세션 속성에 원본 토큰을 저장한다.")
    @Test
    void beforeHandshake_storesAccessToken_whenCookieExists() {
        // given
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        Cookie accessTokenCookie = new Cookie("accessToken", "token-value");
        servletRequest.setCookies(accessTokenCookie);

        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes.get(WebSocketAuthHandshakeInterceptor.ACCESS_TOKEN_KEY))
                .isEqualTo("token-value");
    }

    @DisplayName("쿠키가 없어도 핸드셰이크는 그대로 통과한다.")
    @Test
    void beforeHandshake_passesThrough_whenCookiesMissing() {
        // given
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        // when
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // then
        assertThat(result).isTrue();
        assertThat(attributes).isEmpty();
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
        verifyNoInteractions(response, wsHandler);
    }
}
