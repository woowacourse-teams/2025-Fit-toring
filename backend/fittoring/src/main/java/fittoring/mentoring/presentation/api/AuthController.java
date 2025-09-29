package fittoring.mentoring.presentation.api;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.AuthService;
import fittoring.mentoring.business.service.PhoneVerificationFacadeService;
import fittoring.mentoring.business.service.PhoneVerificationService;
import fittoring.mentoring.presentation.CookieWriter;
import fittoring.mentoring.presentation.dto.*;
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

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid SignInRequest request, HttpServletResponse httpResponse) {
        AuthTokenResponse response = authService.login(request.loginId(), request.password());
        CookieWriter.write(httpResponse, response);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
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

    @PostMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, value = "error_description") String errorDescription,
            @RequestParam(required = false) String state) {
        // TODO : state 검증

        if (error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("카카오 로그인 실패 : " + error + " : " + errorDescription);
        }

        if (code != null) {
            authService.kakaoLogin(code);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
