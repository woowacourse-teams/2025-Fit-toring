package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.AuthService;
import fittoring.mentoring.presentation.dto.SignUpRequest;
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

    @PostMapping("/signup")
    public ResponseEntity<Void> sinUp(@RequestBody SignUpRequest request) {
        authService.register(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
