package fittoring.application.review.presentation;

import fittoring.application.review.presentation.dto.request.ReviewCreateRequest;
import fittoring.application.review.presentation.dto.request.ReviewModifyRequest;
import fittoring.application.review.presentation.dto.response.MemberReviewGetResponse;
import fittoring.application.review.presentation.dto.response.ReviewCreateResponse;
import fittoring.application.review.presentation.dto.response.ReviewGetResponse;
import fittoring.application.review.service.ReviewService;
import fittoring.application.review.service.dto.ReviewCreateDto;
import fittoring.application.review.service.dto.ReviewDeleteDto;
import fittoring.application.review.service.dto.ReviewModifyDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @AuthRequired
    @PostMapping("/reviews")
    public ResponseEntity<ReviewCreateResponse> createReview(
            @Login LoginInfo loginInfo,
            @Valid @RequestBody ReviewCreateRequest requestBody
    ) {
        ReviewCreateDto reviewCreateDto = ReviewCreateDto.of(
                loginInfo.memberId(),
                requestBody
        );
        ReviewCreateResponse responseBody = reviewService.createReview(reviewCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @AuthRequired
    @GetMapping("/reviews/mine")
    public ResponseEntity<List<MemberReviewGetResponse>> findMyReviews(
            @Login LoginInfo loginInfo
    ) {
        List<MemberReviewGetResponse> responseBody = reviewService.findMemberReviews(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @GetMapping("/mentorings/{mentoringId}/reviews")
    public ResponseEntity<List<ReviewGetResponse>> findMentoringReviews(
            @PathVariable("mentoringId") Long mentoringId
    ) {
        List<ReviewGetResponse> responseBody = reviewService.findMentoringReviews(mentoringId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @AuthRequired
    @PatchMapping("reviews/{reviewId}")
    public ResponseEntity<Void> modifyReview(
            @Login LoginInfo loginInfo,
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody ReviewModifyRequest requestBody
    ) {
        ReviewModifyDto reviewModifyDto = ReviewModifyDto.of(
                loginInfo.memberId(),
                reviewId,
                requestBody
        );
        reviewService.modifyReview(reviewModifyDto);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @AuthRequired
    @DeleteMapping("reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @Login LoginInfo loginInfo,
            @PathVariable("reviewId") Long reviewId
    ) {
        ReviewDeleteDto reviewDeleteDto = new ReviewDeleteDto(loginInfo.memberId(), reviewId);
        reviewService.deleteReview(reviewDeleteDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
