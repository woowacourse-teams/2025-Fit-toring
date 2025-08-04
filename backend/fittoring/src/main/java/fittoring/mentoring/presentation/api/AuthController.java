package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.AuthService;
import fittoring.mentoring.presentation.CookieWriter;
import fittoring.mentoring.presentation.dto.AuthTokenResponse;
import fittoring.mentoring.presentation.dto.SignInRequest;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import fittoring.mentoring.presentation.dto.ValidateDuplicateLoginIdRequest;
import jakarta.servlet.http.HttpServletResponse;
import fittoring.mentoring.business.service.PhoneVerificationFacadeService;
import fittoring.mentoring.business.service.PhoneVerificationService;
import fittoring.mentoring.presentation.dto.VerificationCodeRequest;
import fittoring.mentoring.presentation.dto.VerifyPhoneNumberRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final PhoneVerificationFacadeService phoneVerificationFacadeService;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.register(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid SignInRequest request, HttpServletResponse httpResponse) {
        AuthTokenResponse response = authService.login(request.loginId(), request.password());
        CookieWriter.write(httpResponse, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/validate-id")
    public ResponseEntity<Void> validateDuplicateLoginId(@RequestBody @Valid ValidateDuplicateLoginIdRequest request) {
        authService.validateDuplicateLoginId(request.loginId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/auth-code")
    public ResponseEntity<Void> verifyPhoneNumber(@RequestBody @Valid VerifyPhoneNumberRequest request) {
        phoneVerificationFacadeService.sendPhoneVerificationCode(request.phone());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/auth-code/verify")
    public ResponseEntity<Void> verifyPhoneNumber(@RequestBody @Valid VerificationCodeRequest request) {
        phoneVerificationService.verifyCode(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
