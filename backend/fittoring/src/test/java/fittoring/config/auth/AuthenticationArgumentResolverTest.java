package fittoring.config.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

class AuthenticationArgumentResolverTest {

    private final AuthenticationArgumentResolver resolver = new AuthenticationArgumentResolver();

    @DisplayName("memberId request attribute가 있으면 LoginInfo에 값을 담아 반환한다.")
    @Test
    void resolveArgumentWithAuthenticatedMember() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("memberId", 1L);
        NativeWebRequest webRequest = new ServletWebRequest(request);
        MethodParameter parameter = new MethodParameter(
                TestController.class.getDeclaredMethod("endpoint", LoginInfo.class), 0
        );

        Object actual = resolver.resolveArgument(parameter, null, webRequest, null);

        assertThat(actual).isEqualTo(new LoginInfo(1L));
    }

    @DisplayName("memberId request attribute가 없으면 null memberId를 가진 LoginInfo를 반환한다.")
    @Test
    void resolveArgumentWithGuestMember() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        NativeWebRequest webRequest = new ServletWebRequest(request);
        MethodParameter parameter = new MethodParameter(
                TestController.class.getDeclaredMethod("endpoint", LoginInfo.class), 0
        );

        Object actual = resolver.resolveArgument(parameter, null, webRequest, null);

        assertThat(actual).isEqualTo(new LoginInfo(null));
    }

    private static class TestController {

        @SuppressWarnings("unused")
        void endpoint(@Login LoginInfo loginInfo) {
        }
    }
}
