package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.AuthService;
import fittoring.mentoring.business.service.PhoneVerificationFacade;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import fittoring.mentoring.presentation.dto.ValidateDuplicateIdRequest;
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
    private final PhoneVerificationFacade phoneVerificationFacade;

    @PostMapping("/signup")
    public ResponseEntity<Void> sinUp(@RequestBody @Valid SignUpRequest request) {
        authService.register(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/validate-id")
    public ResponseEntity<Void> validateDuplicateId(@RequestBody @Valid ValidateDuplicateIdRequest request) {
        authService.validateDuplicateId(request.id());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/auth-code")
    public ResponseEntity<Void> verifyPhoneNumber(@RequestBody @Valid VerifyPhoneNumberRequest request) {
        phoneVerificationFacade.sendPhoneVerificationCode(request.phone());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
