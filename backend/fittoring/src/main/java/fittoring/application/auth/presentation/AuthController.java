package fittoring.application.auth.presentation;

import fittoring.application.auth.CookieProvider;
import fittoring.application.auth.CookieWriter;
import fittoring.application.auth.presentation.dto.request.OauthSignUpRequest;
import fittoring.application.auth.presentation.dto.request.SignInRequest;
import fittoring.application.auth.presentation.dto.request.SignUpRequest;
import fittoring.application.auth.presentation.dto.request.ValidateDuplicateLoginIdRequest;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.presentation.dto.request.VerifyPhoneNumberRequest;
import fittoring.application.auth.presentation.dto.response.LoginResponse;
import fittoring.application.auth.service.AuthService;
import fittoring.application.auth.service.PhoneVerificationFacadeService;
import fittoring.application.auth.service.PhoneVerificationService;
import fittoring.application.auth.service.dto.AuthTokenDto;
import fittoring.application.auth.service.dto.LoginInfoDto;
import fittoring.application.exception.OauthLoginException;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.MemberOauth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    public static final String KAKAO_STATE = "KAKAO_STATE";

    private final AuthService authService;
    private final PhoneVerificationFacadeService phoneVerificationFacadeService;
    private final PhoneVerificationService phoneVerificationService;

    @Value("${kakao.client-id}")
    private String kakaoClientUrl;

    @Value("${kakao.redirect-url}")
    private String kakaoRedirectUrl;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid SignInRequest request,
                                               HttpServletResponse httpResponse) {
        LoginInfoDto loginInfo = authService.login(request.loginId(), request.password());
        CookieWriter.write(httpResponse, loginInfo.authTokenDto());
        return ResponseEntity.ok(new LoginResponse(loginInfo.memberId()));
    }

    @AuthRequired
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Login LoginInfo loginInfo, HttpServletResponse httpResponse) {
        authService.logout(loginInfo.memberId());
        CookieWriter.clearCookies(httpResponse);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(
            @CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        AuthTokenDto response = authService.reissue(refreshToken);
        CookieWriter.write(httpResponse, response);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/validate-id")
    public ResponseEntity<Void> validateDuplicateLoginId(@RequestBody @Valid ValidateDuplicateLoginIdRequest request) {
        authService.validateDuplicateLoginId(request.loginId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/auth-code")
    public ResponseEntity<Void> verifyPhoneNumber(@RequestBody @Valid VerifyPhoneNumberRequest request) {
        phoneVerificationFacadeService.sendPhoneVerificationCode(request.phone());
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/auth-code/verify")
    public ResponseEntity<Void> verifyPhoneNumber(@RequestBody @Valid VerificationCodeRequest request) {
        phoneVerificationService.verifyCode(request);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @GetMapping("kakao/login")
    public void kakaoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // state 난수 생성
        String state = UUID.randomUUID().toString();

        // 현재 세션에 저장
        request.getSession().setAttribute(KAKAO_STATE, state);

        // redirect url 구성
        String url = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientUrl +
                "&redirect_uri=" + kakaoRedirectUrl +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8) +
                "&response_type=code";

        // redirect
        response.sendRedirect(url);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallBack_V2(
            @RequestParam String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, value = "error_description") String errorDescription,
            @RequestParam(required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        validateOAuthState(state, request);

        // 로그인
        LoginInfoDto loginInfoDto = authService.kakaoLogin(code);
        AuthTokenDto authTokenDto = loginInfoDto.authTokenDto();
        LoginResponse loginResponse = new LoginResponse(loginInfoDto.memberId());

        // 기존 회원 로그인 성공 토큰 응답
        if (authTokenDto.isLoginSuccess()) {
            CookieWriter.write(response, authTokenDto);
            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
        }

        // 신규 회원 카카오 회원가입 토큰 응답
        ResponseCookie oauthCookie = CookieProvider.createCookie("oauthSignUpToken",
                authTokenDto.oauthSignUpToken());
        response.addHeader(HttpHeaders.SET_COOKIE, oauthCookie.toString());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private void validateOAuthState(String state, HttpServletRequest request) {
        // 세션에 저장된 state 불러오기
        String savedState = (String) request.getSession().getAttribute(KAKAO_STATE);

        // 한번 사용 후 제거해주기
        request.getSession().removeAttribute(KAKAO_STATE);

        // state 검증
        if(savedState==null || !savedState.equals(state)){
            throw new IllegalStateException("로그인 세션 불일치 : state 값이 일치하지 않습니다.");
        }
    }

    @PostMapping("/oauth-signup")
    public ResponseEntity<LoginResponse> oauthSignUp(@RequestBody @Valid OauthSignUpRequest request,
                                                     @CookieValue("oauthSignUpToken") String oauthSignUpToken,
                                                     HttpServletResponse httpResponse) {
        MemberOauth memberOauth = authService.registerOauthMember(request, oauthSignUpToken);
        LoginResponse response = new LoginResponse(memberOauth.getMemberId());
        AuthTokenDto authTokenDto = authService.loginOauthMember(memberOauth);
        CookieWriter.clearCookies(httpResponse);
        CookieWriter.write(httpResponse, authTokenDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
