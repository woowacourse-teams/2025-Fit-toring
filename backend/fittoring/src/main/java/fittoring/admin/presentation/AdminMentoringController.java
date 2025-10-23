package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminMentoringResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminMentoringService;
import fittoring.application.mentoring.service.MentoringService;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin/mentorings")
@RestController
public class AdminMentoringController {

    private final MentoringService mentoringService;
    private final AdminMentoringService adminMentoringService;

    @AuthRequired
    @GetMapping
    public ResponseEntity<PageResult<AdminMentoringResponse>> getMentorings(
            @Login LoginInfo loginInfo,
            @RequestParam(defaultValue = "1") int page
    ) {
        PageResult<AdminMentoringResponse> response = adminMentoringService.findAllForAdminPaged(
                loginInfo.memberId(),
                page
        );

        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @DeleteMapping("/{mentoringId}")
    public ResponseEntity<Void> deleteMentoring(@Login LoginInfo loginInfo,
                                                @PathVariable("mentoringId") Long mentoringId) {
        mentoringService.deleteMentoringByAdmin(loginInfo, mentoringId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
