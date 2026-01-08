package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminReviewInfoResponse;
import fittoring.application.review.service.ReviewService;
import fittoring.config.auth.Admin;
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

    @Admin
    @GetMapping("/admin/mentorings/{mentoringId}/reviews")
    public ResponseEntity<AdminReviewInfoResponse> findAllByMentoringForAdmin(
            @PathVariable("mentoringId") Long mentoringId) {
        AdminReviewInfoResponse response = reviewService.findAllByMentoringForAdmin(mentoringId);
        return ResponseEntity.ok(response);
    }

    @Admin
    @DeleteMapping("/admin/reviews/{reviewId}")
    public ResponseEntity<Void> deleteForAdmin(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteForAdmin(reviewId);
        return ResponseEntity.noContent().build();
    }
}
