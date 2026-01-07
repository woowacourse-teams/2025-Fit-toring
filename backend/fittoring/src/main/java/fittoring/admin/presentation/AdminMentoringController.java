package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminMentoringResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminMentoringService;
import fittoring.application.mentoring.service.MentoringService;
import fittoring.config.auth.Admin;
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

    @Admin
    @GetMapping
    public ResponseEntity<PageResult<AdminMentoringResponse>> getMentorings(
            @RequestParam(defaultValue = "1") int page) {
        PageResult<AdminMentoringResponse> response = adminMentoringService.findAllForAdminPaged(page);

        return ResponseEntity.ok(response);
    }

    @Admin
    @DeleteMapping("/{mentoringId}")
    public ResponseEntity<Void> deleteMentoring(@PathVariable("mentoringId") Long mentoringId) {
        mentoringService.deleteMentoringByAdmin(mentoringId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
