package fittoring.mentoring.presentation.api;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("mentorings/{mentoringId}/review")
    public ResponseEntity<ReviewCreateResponse> createReview(
        @PathVariable("mentoringId") Long mentoringId,
        @Valid  @RequestBody ReviewCreateRequest requestBody
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
}
