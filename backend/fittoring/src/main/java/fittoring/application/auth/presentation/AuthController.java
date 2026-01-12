package fittoring.application.auth.presentation;

import fittoring.application.auth.CookieWriter;
import fittoring.application.auth.presentation.dto.request.FindLoginIdRequest;
import fittoring.application.auth.presentation.dto.request.OauthSignUpRequest;
import fittoring.application.auth.presentation.dto.request.ResetPasswordRequest;
import fittoring.application.auth.presentation.dto.request.SignInRequest;
import fittoring.application.auth.presentation.dto.request.SignUpRequest;
import fittoring.application.auth.presentation.dto.request.ValidateDuplicateLoginIdRequest;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.presentation.dto.request.VerifyPhoneNumberRequest;
import fittoring.application.auth.presentation.dto.response.LoginIdResponse;
import fittoring.application.auth.presentation.dto.response.LoginResponse;
import fittoring.application.auth.presentation.dto.response.LoginStatusDto;
import fittoring.application.auth.service.AuthService;
import fittoring.application.auth.service.JwtExtractor;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.auth.service.PhoneVerificationFacadeService;
import fittoring.application.auth.service.PhoneVerificationService;
import fittoring.application.auth.service.dto.AuthTokenDto;
import fittoring.application.auth.service.dto.LoginInfoDto;
import fittoring.application.exception.InvalidTokenException;
import fittoring.application.exception.OauthLoginException;
import fittoring.application.member.service.dto.RegisterOAuthDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final AuthService authService;
    private final PhoneVerificationFacadeService phoneVerificationFacadeService;
    private final PhoneVerificationService phoneVerificationService;
    private final JwtExtractor jwtExtractor;
    private final JwtProvider jwtProvider;
    private final CookieWriter cookieWriter;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-url}")
    private String kakaoRedirectUrl;

    @Value("${client.base-url}")
    private String clientBaseUrl;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.register(request.toRegisterMemberDto());
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid SignInRequest request,
                                               HttpServletResponse httpResponse) {
        LoginInfoDto loginInfo = authService.login(request.loginId(), request.password());
        cookieWriter.write(httpResponse, loginInfo.authTokenDto());
        return ResponseEntity.ok(new LoginResponse(loginInfo.memberId()));
    }

    @AuthRequired
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Login LoginInfo loginInfo, HttpServletResponse httpResponse) {
        authService.logout(loginInfo.memberId());
        cookieWriter.clearCookies(httpResponse);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/auth/check")
    public ResponseEntity<LoginStatusDto> isLoggedIn(HttpServletRequest httpRequest) {
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies == null || cookies.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        String accessToken;
        try {
            accessToken = jwtExtractor.extractTokenFromCookie("accessToken", cookies);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Long memberId = jwtProvider.extractTokenPayload(accessToken).sub();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new LoginStatusDto(memberId));
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(
            @CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        AuthTokenDto response = authService.reissue(refreshToken);
        cookieWriter.write(httpResponse, response);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/validate-login-id")
    public ResponseEntity<Void> validateDuplicateLoginId(@RequestBody @Valid ValidateDuplicateLoginIdRequest request) {
        authService.validateDuplicateLoginId(request.loginId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/auth-code")
    public ResponseEntity<Void> verifyPhoneNumber(@RequestBody @Valid VerifyPhoneNumberRequest request) {
        phoneVerificationFacadeService.sendPhoneVerificationCode(request.phoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/auth-code/verify")
    public ResponseEntity<Void> verifyPhoneNumber(@RequestBody @Valid VerificationCodeRequest request) {
        phoneVerificationService.verifyCode(request);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<Void> redirectKakaoAuth() {
        String state = jwtProvider.createStateToken();

        // redirect url 구성
        URI url = UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoClientId)
                .queryParam("redirect_uri", kakaoRedirectUrl)
                .queryParam("state", URLEncoder.encode(state, StandardCharsets.UTF_8))
                .build()
                .toUri();

        // redirect
        return ResponseEntity.status(HttpStatus.FOUND).location(url).build();
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> kakaoCallBack(
            @RequestParam String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, value = "error_description") String errorDescription,
            @RequestParam(required = false) String state,
            HttpServletResponse response
    ) {
        if (error != null) {
            throw new OauthLoginException("OAuth callback error : " + errorDescription);
        }

        // state 토큰 검증
        jwtProvider.validateToken(state);

        // 로그인
        LoginInfoDto loginInfoDto = authService.kakaoLogin(code);
        AuthTokenDto authTokenDto = loginInfoDto.authTokenDto();

        // 기존 회원 로그인 성공 토큰 응답 & 메인 페이지로 리다이랙트
        if (authTokenDto.isLoginSuccess()) {
            cookieWriter.write(response, authTokenDto);
            URI homeUri = URI.create(clientBaseUrl);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(homeUri)
                    .build();
        }

        // 신규 회원 카카오 회원가입 토큰 응답
        cookieWriter.writeOauthSignUpToken(response, authTokenDto.oauthSignUpToken());

        // OAuth 회원가입 페이지로 리다이랙트
        URI identityVerificationUri = URI.create(clientBaseUrl + "/identity-verification");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(identityVerificationUri)
                .build();
    }

    @PostMapping("/oauth-signup")
    public ResponseEntity<LoginResponse> oauthSignUp(
            @RequestBody @Valid OauthSignUpRequest request,
            @CookieValue("oauthSignUpToken") String oauthSignUpToken,
            HttpServletResponse httpResponse) {
        RegisterOAuthDto registerOAuthDto = authService.registerOauthMember(request, oauthSignUpToken);
        LoginResponse response = new LoginResponse(registerOAuthDto.memberId());
        AuthTokenDto authTokenDto = registerOAuthDto.authTokenDto();
        cookieWriter.clearCookies(httpResponse);
        cookieWriter.write(httpResponse, authTokenDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login-id")
    public ResponseEntity<LoginIdResponse> findLoginId(@RequestBody @Valid FindLoginIdRequest request) {
        String loginId = authService.findLoginId(request.name(), request.phoneNumber());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new LoginIdResponse(loginId));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request.loginId(), request.phoneNumber(), request.password());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
