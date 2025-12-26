package fittoring.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.UnAuthorizedException;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtExtractor jwtExtractor;

    @Mock
    private HandlerMethod handlerMethod;

    @InjectMocks
    private AuthenticationInterceptor interceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @DisplayName("인증 어노테이션이 존재하고 토큰이 유효하면 인증에 성공한다.")
    @Test
    void successAuthentication() {
        // given
        AuthRequired authAnnotation = mock(AuthRequired.class);
        given(handlerMethod.getMethodAnnotation(AuthRequired.class)).willReturn(authAnnotation);

        Cookie cookie = new Cookie("accessToken", "valid-token");
        request.setCookies(cookie);

        given(jwtExtractor.extractTokenFromCookie(anyString(), any())).willReturn("valid-token");
        given(jwtProvider.getSubjectFromPayloadBy("valid-token")).willReturn(1L);

        // when
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(result).isTrue();
            assertThat(request.getAttribute("memberId")).isEqualTo(1L);
        });
    }

    @DisplayName("인증 어노테이션이 존재하고 쿠키가 유효하지 않으면 인증에 실패하고, 예외가 발생한다.")
    @Test
    void failAuthentication() {
        // given
        AuthRequired authAnnotation = mock(AuthRequired.class);
        given(handlerMethod.getMethodAnnotation(AuthRequired.class)).willReturn(authAnnotation);

        // when // then
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod)).isInstanceOf(
                UnAuthorizedException.class).hasMessage(BusinessErrorMessage.EMPTY_COOKIE.getMessage());
    }

    @DisplayName("HTTP 메서드가 OPTIONS이면 인증을 수행하지 않고 true를 반환한다")
    @Test
    void preflightRequestByPass() {
        //given
        request.setMethod("OPTIONS");

        // when
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isTrue();
        verify(jwtExtractor, times(0)).extractTokenFromCookie(anyString(), any());
        verify(jwtProvider, times(0)).getSubjectFromPayloadBy(anyString());
    }

    @DisplayName("인증 어노테이션이 존재하지 않으면 인증을 진행하지 않고, true를 반환한다.")
    @Test
    void nonAnnotationAuthenticationByPass() {
        // given // when
        boolean actual = interceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(actual).isTrue();
        verify(jwtExtractor, times(0)).extractTokenFromCookie(anyString(), any());
    }
}
