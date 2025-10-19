package fittoring.application.auth.presentation;

import fittoring.application.auth.CookieProvider;
import fittoring.application.auth.CookieWriter;
import fittoring.application.auth.presentation.dto.request.OauthSignUpRequest;
import fittoring.application.auth.presentation.dto.request.SignInRequest;
import fittoring.application.auth.presentation.dto.request.SignUpRequest;
import fittoring.application.auth.presentation.dto.request.ValidateDuplicateLoginIdRequest;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.presentation.dto.request.VerifyPhoneNumberRequest;
import fittoring.application.auth.presentation.dto.response.AuthTokenResponse;
import fittoring.application.auth.presentation.dto.response.LoginResponse;
import fittoring.application.auth.service.AuthService;
import fittoring.application.auth.service.PhoneVerificationFacadeService;
import fittoring.application.auth.service.PhoneVerificationService;
import fittoring.application.exception.OauthLoginException;
import fittoring.application.mentoring.presentation.dto.response.MemberLoginResponse;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.MemberOauth;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final AuthService authService;
    private final PhoneVerificationFacadeService phoneVerificationFacadeService;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody @Valid SignInRequest request,
                                                     HttpServletResponse httpResponse) {
        LoginResponse response = authService.login(request.loginId(), request.password());
        CookieWriter.write(httpResponse, response.authToken());
        return ResponseEntity.ok(response.memberLoginResponse());
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
        AuthTokenResponse response = authService.reissue(refreshToken);
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

    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> kakaoCallBackSuccess() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(
            @RequestParam String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, value = "error_description") String errorDescription,
            @RequestParam(required = false) String state,
            HttpServletResponse httpResponse
    ) {
        // TODO : state 검증
        if (error != null) {
            throw new OauthLoginException("카카오 로그인 에러 : " + error + " : " + errorDescription);
        }

        AuthTokenResponse authTokenResponse = authService.kakaoLogin(code);

        if (authTokenResponse.isLoginSuccess()) {
            CookieWriter.write(httpResponse, authTokenResponse);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        ResponseCookie oauthCookie = CookieProvider.createCookie("oauthSignUpToken",
                authTokenResponse.oauthSignUpToken());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, oauthCookie.toString());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/oauth-signup")
    public ResponseEntity<Void> oauthSignUp(@RequestBody @Valid OauthSignUpRequest request,
                                            @CookieValue("oauthSignUpToken") String oauthSignUpToken,
                                            HttpServletResponse httpResponse) {
        MemberOauth memberOauth = authService.registerOauthMember(request, oauthSignUpToken);
        AuthTokenResponse authTokenResponse = authService.loginOauthMember(memberOauth);
        CookieWriter.clearCookies(httpResponse);
        CookieWriter.write(httpResponse, authTokenResponse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
