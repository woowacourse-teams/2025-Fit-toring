package fittoring.application.auth.presentation;

import fittoring.application.auth.CookieWriter;
import fittoring.application.auth.presentation.dto.request.*;
import fittoring.application.auth.presentation.dto.response.LoginResponse;
import fittoring.application.auth.service.AuthService;
import fittoring.application.auth.service.PhoneVerificationFacadeService;
import fittoring.application.auth.service.PhoneVerificationService;
import fittoring.application.auth.service.dto.AuthTokenDto;
import fittoring.application.auth.service.dto.LoginInfoDto;
import fittoring.application.exception.OauthLoginException;
import fittoring.application.member.service.dto.RegisterOAuthDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final AuthService authService;
    private final PhoneVerificationFacadeService phoneVerificationFacadeService;
    private final PhoneVerificationService phoneVerificationService;
    private final CookieWriter cookieWriter;

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

        LoginInfoDto loginInfoDto = authService.kakaoLogin(code);
        AuthTokenDto authTokenDto = loginInfoDto.authTokenDto();
        LoginResponse loginResponse = new LoginResponse(loginInfoDto.memberId());

        if (authTokenDto.isLoginSuccess()) {
            cookieWriter.write(httpResponse, authTokenDto);
            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
        }

        cookieWriter.writeOauthSignUpToken(httpResponse, authTokenDto.oauthSignUpToken());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
}
