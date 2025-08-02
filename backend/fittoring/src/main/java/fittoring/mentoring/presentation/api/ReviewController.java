package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.ReviewService;
import fittoring.mentoring.business.service.dto.MemberReviewGetDto;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.business.service.dto.ReviewDeleteDto;
import fittoring.mentoring.business.service.dto.ReviewModifyDto;
import fittoring.mentoring.presentation.dto.MemberReviewGetResponse;
import fittoring.mentoring.presentation.dto.ReviewCreateRequest;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import fittoring.mentoring.presentation.dto.ReviewModifyRequest;
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

    @PostMapping("mentorings/{mentoringId}/reviews")
    public ResponseEntity<ReviewCreateResponse> createReview(
        // TODO: 로그인 정보 받기
        @PathVariable("mentoringId") Long mentoringId,
        @Valid @RequestBody ReviewCreateRequest requestBody
    ) {
        ReviewCreateDto reviewCreateDto = ReviewCreateDto.of(
            1L,
            mentoringId,
            requestBody
        );
        ReviewCreateResponse responseBody = reviewService.createReview(reviewCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(responseBody);
    }

    @GetMapping("reviews/mine")
    public ResponseEntity<List<MemberReviewGetResponse>> findMyReviews(
        // TODO: 로그인 정보 받기
    ) {
        MemberReviewGetDto memberReviewGetDto = new MemberReviewGetDto(1L);
        List<MemberReviewGetResponse> responseBody = reviewService.findMemberReviews(memberReviewGetDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(responseBody);
    }

    @PatchMapping("reviews/{reviewId}")
    public ResponseEntity<Void> modifyReview(
        // TODO: 로그인 정보 받기
        @PathVariable("reviewId") Long reviewId,
        @Valid @RequestBody ReviewModifyRequest requestBody
    ) {
        ReviewModifyDto reviewModifyDto = ReviewModifyDto.of(
            1L,
            reviewId,
            requestBody
        );
        reviewService.modifyReview(reviewModifyDto);
        return ResponseEntity.status(HttpStatus.OK)
            .build();
    }

    @DeleteMapping("reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
        // TODO: 로그인 정보 받기
        @PathVariable("reviewId") Long reviewId
    ) {
        ReviewDeleteDto reviewDeleteDto = new ReviewDeleteDto(1L, reviewId);
        reviewService.deleteReview(reviewDeleteDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }
}
