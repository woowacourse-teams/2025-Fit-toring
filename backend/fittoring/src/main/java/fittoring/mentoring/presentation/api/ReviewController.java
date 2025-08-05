package fittoring.mentoring.presentation.api;

import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.ReviewService;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.presentation.dto.ReviewCreateRequest;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("mentorings/{mentoringId}/review")
    public ResponseEntity<ReviewCreateResponse> createReview(
        @PathVariable("mentoringId") Long mentoringId,
        @Valid  @RequestBody ReviewCreateRequest requestBody,
        @Login LoginInfo loginInfo
    ) {
        ReviewCreateDto reviewCreateDto = ReviewCreateDto.of(
            loginInfo.memberId(),
            mentoringId,
            requestBody
        );
        ReviewCreateResponse responseBody = reviewService.createReview(reviewCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(responseBody);
    }
}