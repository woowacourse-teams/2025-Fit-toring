package fittoring.application.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.application.auth.CookieWriter;
import fittoring.application.auth.presentation.dto.request.OauthSignUpRequest;
import fittoring.application.auth.service.AuthFacadeService;
import fittoring.application.auth.service.AuthService;
import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.PhoneVerificationFacadeService;
import fittoring.application.auth.service.PhoneVerificationService;
import fittoring.application.auth.service.dto.AuthTokenDto;
import fittoring.application.member.service.dto.RegisterOAuthDto;
import fittoring.domain.model.Gender;
import fittoring.logging.ErrorJsonLogger;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockitoBean
    private PhoneVerificationFacadeService phoneVerificationFacadeService;

    @MockitoBean
    private AuthFacadeService authFacadeService;

    @MockitoBean
    private PhoneVerificationService phoneVerificationService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CookieWriter cookieWriter;

    @MockitoBean
    private JwtExtractor jwtExtractor;

    @MockitoBean
    private ErrorJsonLogger errorJsonLogger;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("소셜 회원가입 API(/oauth-signup)")
    class OauthSignUp {
        @DisplayName("정상적인 요청에 대해 201 Created와 회원 ID를 반환한다")
        @Test
        void return201AndMemberId() throws Exception {
            //given
            OauthSignUpRequest request = new OauthSignUpRequest("정상적인이름", Gender.MALE, "010-1234-5678");
            AuthTokenDto tokenDto = new AuthTokenDto("access", "refresh", null);
            RegisterOAuthDto result = new RegisterOAuthDto(1L, tokenDto);

            given(authService.registerOauthMember(any(), any())).willReturn(result);

            //when //then
            mockMvc.perform(post("/oauth-signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("oauthSignUpToken", "valid-token"))
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.memberId").value(1L));
        }

        @DisplayName("이름이 비어있으면 400 Bad Request를 반환한다")
        @ParameterizedTest
        @ValueSource(strings = {" "})
        void givenBlankName_thenReturn400(String blankName) throws Exception {
            //given
            OauthSignUpRequest request = new OauthSignUpRequest(blankName, Gender.MALE, "010-1234-5678");

            //when //then
            mockMvc.perform(post("/oauth-signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("oauthSignUpToken", "valid-token"))
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("성별이 없으면 400 Bad Request를 반환한다")
        @Test
        void givenNullGender_thenReturn400() throws Exception {
            //given
            OauthSignUpRequest request = new OauthSignUpRequest("정상적인이름", null, "010-1234-5678");

            //when //then
            mockMvc.perform(post("/oauth-signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("oauthSignUpToken", "valid-token"))
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("전화번호가 비어있으면 400 Bad Request를 반환한다")
        @ParameterizedTest
        @ValueSource(strings = {" "})
        void givenBlankPhone_thenReturn400(String blankPhone) throws Exception {
            //given
            OauthSignUpRequest request = new OauthSignUpRequest("정상적인이름", Gender.MALE, blankPhone);

            //when //then
            mockMvc.perform(post("/oauth-signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("oauthSignUpToken", "valid-token"))
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("전화번호 형식이 올바르지 않으면 400 Bad Request를 반환한다")
        @ParameterizedTest
        @ValueSource(strings = {"01012345678", "abc-defg-hijk"})
        void givenInvalidPhoneFormat_thenReturn400(String invalidPhone) throws Exception {
            //given
            OauthSignUpRequest request = new OauthSignUpRequest("정상적인이름", Gender.MALE, invalidPhone);

            //when //then
            mockMvc.perform(post("/oauth-signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("oauthSignUpToken", "valid-token"))
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
