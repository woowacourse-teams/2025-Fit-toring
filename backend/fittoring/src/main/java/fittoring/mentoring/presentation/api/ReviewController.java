package fittoring.mentoring.presentation.api;

import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.ReviewService;
import fittoring.mentoring.business.service.dto.MemberReviewGetDto;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.presentation.dto.MemberReviewGetResponse;
import fittoring.mentoring.presentation.dto.ReviewCreateRequest;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<ReviewCreateResponse> createReview(
        @Login LoginInfo loginInfo,
        @Valid  @RequestBody ReviewCreateRequest requestBody
    ) {
        ReviewCreateDto reviewCreateDto = ReviewCreateDto.of(
            loginInfo.memberId(),
            requestBody
        );
        ReviewCreateResponse responseBody = reviewService.createReview(reviewCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(responseBody);
    }

    @GetMapping("reviews/mine")
    public ResponseEntity<List<MemberReviewGetResponse>> findMyReviews(
        @Login LoginInfo loginInfo
    ) {
        MemberReviewGetDto memberReviewGetDto = new MemberReviewGetDto(loginInfo.memberId());
        List<MemberReviewGetResponse> responseBody = reviewService.findMemberReviews(memberReviewGetDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(responseBody);
    }
}
