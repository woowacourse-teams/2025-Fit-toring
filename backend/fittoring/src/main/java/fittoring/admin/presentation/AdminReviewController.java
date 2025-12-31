package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminReviewInfoResponse;
import fittoring.application.review.service.ReviewService;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @AuthRequired
    @DeleteMapping("/admin/reviews/{reviewId}")
    public ResponseEntity<Void> deleteForAdmin(
            @Login LoginInfo loginInfo,
            @PathVariable("reviewId") Long reviewId
    ) {
        reviewService.deleteForAdmin(loginInfo.memberId(), reviewId);
        return ResponseEntity.noContent().build();
    }
}
