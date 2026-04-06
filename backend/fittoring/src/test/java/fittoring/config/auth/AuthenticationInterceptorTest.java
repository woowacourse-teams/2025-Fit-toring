package fittoring.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.TokenPayload;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.UnauthorizedException;
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
        lenient().when(handlerMethod.hasMethodAnnotation(OptionalAuth.class)).thenReturn(false);
    }

    @DisplayName("인증 어노테이션이 존재하고 토큰이 유효하면 인증에 성공한다.")
    @Test
    void successAuthentication() {
        // given
        given(handlerMethod.hasMethodAnnotation(AuthRequired.class)).willReturn(true);

        Cookie cookie = new Cookie("accessToken", "valid-token");
        request.setCookies(cookie);

        given(jwtExtractor.extractTokenFromCookie(anyString(), any())).willReturn("valid-token");
        given(jwtProvider.extractTokenPayload("valid-token")).willReturn(
                new TokenPayload(1L, "MENTEE")
        );

        // when
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).isTrue();
            softly.assertThat(request.getAttribute("memberId")).isEqualTo(1L);
        });
    }

    @DisplayName("어드민 어노테이션이 존재하고 어드민 권한이 있으면 인증에 성공한다.")
    @Test
    void successAdminAuthentication() {
        // given
        given(handlerMethod.hasMethodAnnotation(Admin.class)).willReturn(true);
        given(handlerMethod.hasMethodAnnotation(AuthRequired.class)).willReturn(false);

        Cookie cookie = new Cookie("accessToken", "valid-token");
        request.setCookies(cookie);

        given(jwtExtractor.extractTokenFromCookie(anyString(), any())).willReturn("valid-token");
        given(jwtProvider.extractTokenPayload("valid-token")).willReturn(new TokenPayload(1L, "ADMIN"));

        // when
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).isTrue();
            softly.assertThat(request.getAttribute("memberId")).isEqualTo(1L);
        });
    }

    @DisplayName("어드민 어노테이션이 존재하고 어드민 권한이 없으면 예외가 발생한다.")
    @Test
    void failAdminAuthentication() {
        // given
        given(handlerMethod.hasMethodAnnotation(Admin.class)).willReturn(true);
        given(handlerMethod.hasMethodAnnotation(AuthRequired.class)).willReturn(false);

        Cookie cookie = new Cookie("accessToken", "valid-token");
        request.setCookies(cookie);

        given(jwtExtractor.extractTokenFromCookie(anyString(), any())).willReturn("valid-token");
        given(jwtProvider.extractTokenPayload("valid-token"))
                .willReturn(new TokenPayload(1L, "MENTEE"));

        // when // then
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(ForbiddenException.class).hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("인증 어노테이션이 존재하고 쿠키가 유효하지 않으면 인증에 실패하고, 예외가 발생한다.")
    @Test
    void failAuthentication() {
        // given
        given(handlerMethod.hasMethodAnnotation(AuthRequired.class)).willReturn(true);

        // when // then
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(UnauthorizedException.class).hasMessage(BusinessErrorMessage.EMPTY_COOKIE.getMessage());
    }

    @DisplayName("인증 어노테이션과 어드민 어노테이션이 모두 존재하고 어드민 권한이 있으면 인증에 성공한다.")
    @Test
    void successAdminAuthenticationWithAuthRequired() {
        // given
        given(handlerMethod.hasMethodAnnotation(Admin.class)).willReturn(true);
        given(handlerMethod.hasMethodAnnotation(AuthRequired.class)).willReturn(true);

        Cookie cookie = new Cookie("accessToken", "valid-token");
        request.setCookies(cookie);

        given(jwtExtractor.extractTokenFromCookie(anyString(), any())).willReturn("valid-token");
        given(jwtProvider.extractTokenPayload("valid-token")).willReturn(new TokenPayload(1L, "ADMIN"));

        // when
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).isTrue();
            softly.assertThat(request.getAttribute("memberId")).isEqualTo(1L);
        });
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
        verify(jwtProvider, times(0)).extractTokenPayload(anyString());
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

    @DisplayName("OptionalAuth가 있으면 유효한 토큰이 있을 때 memberId를 바인딩한다.")
    @Test
    void optionalAuthSuccess() {
        // given
        given(handlerMethod.hasMethodAnnotation(OptionalAuth.class)).willReturn(true);

        Cookie cookie = new Cookie("accessToken", "valid-token");
        request.setCookies(cookie);

        given(jwtExtractor.extractTokenFromCookie(anyString(), any())).willReturn("valid-token");
        given(jwtProvider.extractTokenPayload("valid-token")).willReturn(new TokenPayload(1L, "MENTEE"));

        // when
        boolean actual = interceptor.preHandle(request, response, handlerMethod);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isTrue();
            softly.assertThat(request.getAttribute("memberId")).isEqualTo(1L);
        });
    }

    @DisplayName("OptionalAuth가 있으면 토큰이 없어도 예외 없이 통과한다.")
    @Test
    void optionalAuthFallbackToGuest() {
        // given
        given(handlerMethod.hasMethodAnnotation(OptionalAuth.class)).willReturn(true);

        // when
        boolean actual = interceptor.preHandle(request, response, handlerMethod);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isTrue();
            softly.assertThat(request.getAttribute("memberId")).isNull();
        });
        verify(jwtExtractor, times(0)).extractTokenFromCookie(anyString(), any());
    }
}
