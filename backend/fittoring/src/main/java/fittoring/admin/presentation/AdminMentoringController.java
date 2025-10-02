package fittoring.admin.presentation;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.application.service.MentoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminMentoringController {

    private final MentoringService mentoringService;

    @AuthRequired
    @DeleteMapping("/admin/mentorings/{mentoringId}")
    public ResponseEntity<Void> deleteMentoring(@Login LoginInfo loginInfo,
                                                @PathVariable("mentoringId") Long mentoringId) {
        mentoringService.deleteMentoringByAdmin(loginInfo, mentoringId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }
}
