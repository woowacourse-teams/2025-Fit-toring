package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.AuthService;
import fittoring.mentoring.presentation.CookieProvider;
import fittoring.mentoring.presentation.dto.AuthTokenResponse;
import fittoring.mentoring.presentation.dto.SignInRequest;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import fittoring.mentoring.presentation.dto.ValidateDuplicateLoginIdRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.register(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> login(@RequestBody @Valid SignInRequest request, HttpServletResponse httpResponse) {
        AuthTokenResponse response = authService.login(request.loginId(), request.password());
        setCookie(httpResponse, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/validate-id")
    public ResponseEntity<Void> validateDuplicateLoginId(@RequestBody @Valid ValidateDuplicateLoginIdRequest request) {
        authService.validateDuplicateLoginId(request.loginId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void setCookie(HttpServletResponse httpResponse, AuthTokenResponse response) {
        ResponseCookie accessTokenCookie = CookieProvider.createCookie(("accessToken"), response.accessToken());
        ResponseCookie refreshTokenCookie = CookieProvider.createCookie(("refreshToken"), response.refreshToken());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}
