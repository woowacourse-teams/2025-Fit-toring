package fittoring.application.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.ReservationNotCompletedException;
import fittoring.application.exception.ReservationNotFoundException;
import fittoring.application.exception.ReviewAlreadyExistsException;
import fittoring.application.exception.ReviewNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.review.presentation.dto.response.MemberReviewGetResponse;
import fittoring.application.review.presentation.dto.response.ReviewCreateResponse;
import fittoring.application.review.presentation.dto.response.ReviewGetResponse;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.application.review.service.dto.ReviewCreateDto;
import fittoring.application.review.service.dto.ReviewDeleteDto;
import fittoring.application.review.service.dto.ReviewModifyDto;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class ReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("리뷰 작성을 성공하면 별점과 리뷰 내용, 리뷰를 작성한 멘토링의 id을 반환한다")
    @Test
    void createReview() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        MentoringStatistics mentoringStatistics = mentoringStatisticsRepository.save(
                MentoringStatistics.defaultOf(mentoring));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));

        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
                mentee.getId(),
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );
        long originalReviewCount = mentoringStatistics.getReviewCount();
        long originalRatingSum = mentoringStatistics.getRatingSum();

        // when
        ReviewCreateResponse reviewCreateResponse = reviewService.createReview(reviewCreateDto);

        // then
        assertSoftly(softly -> {
            softly.assertThat(reviewCreateResponse.mentoringId()).isEqualTo(mentoring.getId());
            softly.assertThat(reviewCreateResponse.rating()).isEqualTo(reviewCreateDto.rating());
            softly.assertThat(reviewCreateResponse.content()).isEqualTo(reviewCreateDto.content());
            softly.assertThat(
                    mentoringStatisticsRepository.findById(mentoring.getId()).orElseThrow().getReviewCount()
            ).isEqualTo(originalReviewCount + 1);
            softly.assertThat(
                    mentoringStatisticsRepository.findById(mentoring.getId()).orElseThrow().getRatingSum()
            ).isEqualTo(originalRatingSum + reviewCreateDto.rating());
        });
    }

    @DisplayName("존재하지 않는 멤버의 요청이라면 예외가 발생한다.")
    @Test
    void createReviewFail1() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));

        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));

        Long invalidMemberId = 999L;
        ReviewCreateDto dto = new ReviewCreateDto(
                invalidMemberId,
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("신청하지 않았던 멘토링에 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReviewFail2() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));

        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));

        Member anotherMember = memberRepository.save(FixtureUtil.testMentee(2));

        ReviewCreateDto dto = new ReviewCreateDto(
                anotherMember.getId(),
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    @DisplayName("이미 리뷰를 작성했던 멘토링에 중복으로 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReviewFail3() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));

        ReviewCreateDto dto = new ReviewCreateDto(
                mentee.getId(),
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        reviewService.createReview(dto);

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(ReviewAlreadyExistsException.class)
                .hasMessage(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
    }

    @DisplayName("멘토링이 완료되지 않은 예약에는 리뷰를 남길 수 없다")
    @Test
    void createReviewFail4() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(FixtureUtil.testPendingReservation(mentoring, mentee));

        ReviewCreateDto dto = new ReviewCreateDto(
                mentee.getId(),
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(ReservationNotCompletedException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_NOT_COMPLETED.getMessage());
    }

    @DisplayName("특정 멤버의 리뷰를 모두 조회 성공 시 리뷰 정보를 반환한다")
    @Test
    void findMemberReviews() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Member mentor1 = memberRepository.save(FixtureUtil.testMentor());
        Member mentor2 = memberRepository.save(FixtureUtil.testMentor(2));

        Mentoring mentoring1 = mentoringRepository.save(FixtureUtil.testMentoring(mentor1));
        Mentoring mentoring2 = mentoringRepository.save(FixtureUtil.testMentoring(mentor2));

        Reservation reservation1 = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring1, mentee));
        Reservation reservation2 = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring2, mentee));

        Review review1 = reviewRepository.save(FixtureUtil.testReview(reservation1, mentee));
        Review review2 = reviewRepository.save(FixtureUtil.testReview(reservation2, mentee));

        List<MemberReviewGetResponse> expected = List.of(
                new MemberReviewGetResponse(
                        review1.getId(),
                        review1.getCreatedAt().toLocalDate(),
                        review1.getRating(),
                        review1.getContent()
                ),
                new MemberReviewGetResponse(
                        review2.getId(),
                        review2.getCreatedAt().toLocalDate(),
                        review2.getRating(),
                        review2.getContent()
                )
        );

        // when
        List<MemberReviewGetResponse> actual = reviewService.findMemberReviews(mentee.getId());

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("특정 멘토링에 달린 리뷰 조회 성공 시 리뷰 정보를 생성일자 내림차순으로 반환한다")
    @Test
    void findMentoringReviews() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));

        Member mentee1 = memberRepository.save(FixtureUtil.testMentee(1));
        Member mentee2 = memberRepository.save(FixtureUtil.testMentee(2));

        Reservation reservation1 = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee1));
        Reservation reservation2 = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee1));
        Reservation reservation3 = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee2));
        Reservation reservation4 = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee2));

        insertReviewUsingNativeQuery(
                2, "최고의 멘토링이었습니다.",
                LocalDateTime.of(2025, 9, 1, 10, 0, 0),
                reservation1, mentee1
        );
        insertReviewUsingNativeQuery(
                2, "최고의 멘토링이었습니다.",
                LocalDateTime.of(2025, 9, 2, 9, 0, 0),
                reservation2, mentee1
        );
        insertReviewUsingNativeQuery(
                2, "최고의 멘토링이었습니다.",
                LocalDateTime.of(2025, 9, 3, 10, 0, 0),
                reservation3, mentee2
        );
        insertReviewUsingNativeQuery(
                2, "최고의 멘토링이었습니다.",
                LocalDateTime.of(2025, 9, 3, 9, 0, 0),
                reservation4, mentee2
        );

        // when
        List<ReviewGetResponse> actual = reviewService.findMentoringReviews(mentoring.getId());

        // then
        assertSoftly(softly -> {
            assertThat(actual).containsExactly(
                    new ReviewGetResponse(
                            3L,
                            "이름",
                            LocalDate.of(2025, 9, 3),
                            2,
                            "최고의 멘토링이었습니다."
                    ),
                    new ReviewGetResponse(
                            4L,
                            "이름",
                            LocalDate.of(2025, 9, 3),
                            2,
                            "최고의 멘토링이었습니다."
                    ),
                    new ReviewGetResponse(
                            2L,
                            "이름",
                            LocalDate.of(2025, 9, 2),
                            2,
                            "최고의 멘토링이었습니다."
                    ),
                    new ReviewGetResponse(
                            1L,
                            "이름",
                            LocalDate.of(2025, 9, 1),
                            2,
                            "최고의 멘토링이었습니다."
                    )
            );
        });
    }

    public Review insertReviewUsingNativeQuery(
            int rating,
            String content,
            LocalDateTime createdAt,
            Reservation reservation,
            Member mentee
    ) {
        jdbcTemplate.update(
                "INSERT INTO review (rating, content, created_at, is_deleted, deleted_at, reservation_id, mentee_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                rating,
                content,
                createdAt,
                false,
                null,
                reservation.getId(),
                mentee.getId()
        );

        Long insertedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        return reviewRepository.findById(insertedId).orElseThrow();
    }

    @DisplayName("본인이 남긴 리뷰의 별점을 수정한다")
    @Test
    void modifyReview1() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));

        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));
        String originalContent = review.getContent();

        int newRating = 2;
        ReviewModifyDto dto = new ReviewModifyDto(
                mentee.getId(),
                review.getId(),
                newRating,
                null
        );

        // when
        reviewService.modifyReview(dto);

        // then
        Review updated = reviewRepository.findById(review.getId())
                .orElseThrow(null);
        assertSoftly(softly -> {
            softly.assertThat(updated.getRating()).isEqualTo(newRating);
            softly.assertThat(updated.getContent()).isEqualTo(originalContent);
        });
    }

    @DisplayName("본인이 남긴 리뷰의 별점을 수정한다")
    @ValueSource(strings = {"", " "})
    @ParameterizedTest
    void modifyReview2(String newString) {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));

        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));
        String originalContent = review.getContent();

        int newRating = 2;
        ReviewModifyDto dto = new ReviewModifyDto(
                mentee.getId(),
                review.getId(),
                newRating,
                newString
        );

        // when
        reviewService.modifyReview(dto);

        // then
        Review updated = reviewRepository.findById(review.getId())
                .orElseThrow(null);
        assertSoftly(softly -> {
            softly.assertThat(updated.getRating()).isEqualTo(newRating);
            softly.assertThat(updated.getContent()).isEqualTo(originalContent);
        });
    }

    @DisplayName("본인이 남긴 리뷰의 내용을 수정한다")
    @Test
    void modifyReview3() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));

        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));
        int originalRating = review.getRating();

        String newContent = "생각해 보니 비용이 너무 비쌌던 것 같아요";
        ReviewModifyDto dto = new ReviewModifyDto(
                mentee.getId(),
                review.getId(),
                null,
                newContent
        );

        // when
        reviewService.modifyReview(dto);

        // then
        Review updated = reviewRepository.findById(review.getId())
                .orElseThrow(null);
        assertSoftly(softly -> {
            softly.assertThat(updated.getRating()).isEqualTo(originalRating);
            softly.assertThat(updated.getContent()).isEqualTo(newContent);
        });
    }

    @DisplayName("본인이 남긴 리뷰의 별점과 내용을 수정한다")
    @Test
    void modifyReview4() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));
        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));

        int newRating = 2;
        String newContent = "생각해 보니 비용이 너무 비쌌던 것 같아요";
        ReviewModifyDto dto = new ReviewModifyDto(
                mentee.getId(),
                review.getId(),
                newRating,
                newContent
        );

        // when
        reviewService.modifyReview(dto);

        // then
        Review updated = reviewRepository.findById(review.getId())
                .orElseThrow(null);
        assertSoftly(softly -> {
            softly.assertThat(updated.getRating()).isEqualTo(newRating);
            softly.assertThat(updated.getContent()).isEqualTo(newContent);
        });
    }

    @DisplayName("존재하지 않는 리뷰를 수정하려고 하면 예외가 발생한다")
    @Test
    void modifyReviewFail1() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        ReviewModifyDto dto = new ReviewModifyDto(
                mentee.getId(),
                999L,
                2,
                "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.modifyReview(dto))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 수정하려고 하면 예외가 발생한다")
    @Test
    void modifyReviewFail2() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));
        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));

        // 리뷰 작성자가 아닌 다른 멤버
        Member invalidMember = memberRepository.save(FixtureUtil.testMentee(2));

        ReviewModifyDto dto = new ReviewModifyDto(
                invalidMember.getId(),
                review.getId(),
                2,
                "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.modifyReview(dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.NOT_REVIEW_OWNER.getMessage());
    }

    @DisplayName("존재하지 않는 리뷰 삭제 요청 시 예외가 발생한다")
    @Test
    void deleteReviewFail1() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        ReviewDeleteDto dto = new ReviewDeleteDto(mentee.getId(), 999L); // 존재하지 않는 리뷰 ID

        // when
        // then
        assertThatThrownBy(() -> reviewService.deleteReview(dto))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 삭제하려고 하면 예외가 발생한다")
    @Test
    void deleteReviewFail2() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));
        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));

        Member invalidMember = memberRepository.save(FixtureUtil.testMentee(2));

        ReviewDeleteDto dto = new ReviewDeleteDto(invalidMember.getId(), review.getId());

        // when
        // then
        assertThatThrownBy(() -> reviewService.deleteReview(dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.NOT_REVIEW_OWNER.getMessage());
    }

    @DisplayName("존재하지 않는 리뷰에 대해 삭제를 요청하면 예외가 발생한다.")
    @Test
    void failNotFoundReviewDelete() {
        // given
        Member admin = memberRepository.save(FixtureUtil.testAdmin());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteForAdmin(1L))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("관리자가 존재하는 리뷰에 대해 삭제를 요청하면 정상적으로 삭제한다.")
    @Test
    void successReviewDelete() {
        // given
        Member admin = memberRepository.save(FixtureUtil.testAdmin());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Member mentor = memberRepository.save(FixtureUtil.testMentor());

        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));
        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));

        // when
        reviewService.deleteForAdmin(review.getId());

        // then
        assertThat(reviewRepository.findById(review.getId())).isEmpty();
    }

    @DisplayName("리뷰를 삭제하면 삭제가 일어난 시간과 함께 삭제상태로 변경된다.")
    @Test
    void deleteReview() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        MentoringStatistics stats = mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Reservation reservation = reservationRepository.save(
                FixtureUtil.testCompletedReservation(mentoring, mentee));
        Review review = reviewRepository.save(FixtureUtil.testReview(reservation, mentee));

        ReviewDeleteDto dto = new ReviewDeleteDto(mentee.getId(), review.getId());
        long originalReviewCount = stats.getReviewCount();
        long originalRatingSum = stats.getRatingSum();

        // when
        reviewService.deleteReview(dto);

        Review deletedReview = reviewRepository.findDeletedById(review.getId());

        assertSoftly(softly -> {
            softly.assertThat(deletedReview.isDeleted()).isTrue();
            softly.assertThat(deletedReview.getDeletedAt()).isNotNull();
            softly.assertThat(mentoringStatisticsRepository.findById(mentoring.getId()).get().getReviewCount())
                    .isEqualTo(originalReviewCount - 1);
            softly.assertThat(mentoringStatisticsRepository.findById(mentoring.getId()).get().getRatingSum())
                    .isEqualTo(originalRatingSum - review.getRating());
        });
    }

    @DisplayName("동시에 300개의 리뷰가 등록될 때 평점 통계가 정확히 반영되어야 한다.")
    @Test
    void reviewStatisticsDecimalPrecisionTest() throws InterruptedException {
        // given
        int threadCount = 300;
        ExecutorService executorService = Executors.newFixedThreadPool(64);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        int[] ratings = {1, 1, 3, 4, 5};

        // when
        for (int i = 0; i < threadCount; i++) {
            int index = i;
            int rating = ratings[i % 5];

            executorService.execute(() -> {
                try {
                    Member mentee = memberRepository.save(FixtureUtil.testMentee(index));
                    Reservation reservation = reservationRepository.save(
                            FixtureUtil.testCompletedReservation(mentoring, mentee));

                    ReviewCreateDto dto = new ReviewCreateDto(
                            mentee.getId(),
                            reservation.getId(),
                            rating,
                            "소수점 테스트 점수: " + rating
                    );

                    reviewService.createReview(dto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        MentoringStatistics stats = mentoringStatisticsRepository.findById(mentoring.getId()).orElseThrow();

        long expectedSum = 840L;
        double expectedAvg = 2.8;

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(stats.getReviewCount()).isEqualTo(threadCount);
            softly.assertThat(stats.getRatingSum()).isEqualTo(expectedSum);
            softly.assertThat(stats.getAverageRating()).isEqualTo(expectedAvg);
        });
    }
}
