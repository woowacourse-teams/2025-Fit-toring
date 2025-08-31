package fittoring.mentoring.presentation.api.admin;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.ReviewService;
import fittoring.mentoring.presentation.dto.AdminReviewInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminReviewController {

    private final ReviewService reviewService;

    @AuthRequired
    @GetMapping("/admin/mentorings/{mentoringId}/reviews")
    public ResponseEntity<AdminReviewInfoResponse> findAllByMentoringForAdmin(
            @Login LoginInfo loginInfo,
            @PathVariable("mentoringId") Long mentoringId
    ) {
        AdminReviewInfoResponse response = reviewService.findAllByMentoringForAdmin(
                loginInfo.memberId(),
                mentoringId
        );
        return ResponseEntity.ok(response);
    }
}
