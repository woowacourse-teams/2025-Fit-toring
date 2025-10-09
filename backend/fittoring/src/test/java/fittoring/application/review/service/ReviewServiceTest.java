package fittoring.application.review.service;

import fittoring.application.FixtureUtil;
import fittoring.application.exception.*;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
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
import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.*;
import fittoring.util.DbCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, ReviewService.class, JpaConfiguration.class, QueryDslConfig.class, MentoringPaginationHelper.class})
@DataJpaTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("리뷰 작성을 성공하면 별점과 리뷰 내용, 리뷰를 작성한 멘토링의 id을 반환한다")
    @Test
    void createReview() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        MentoringStatistics mentoringStatistics = em.persist(MentoringStatistics.defaultOf(mentoring));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));

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
        em.flush();
        em.clear();

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
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));

        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));

        Long invalidMemberId = 999L;
        ReviewCreateDto dto = new ReviewCreateDto(
                invalidMemberId,
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("신청하지 않았던 멘토링에 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReviewFail2() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));

        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));

        Member anotherMember = em.persist(FixtureUtil.getTestMentee(2));

        ReviewCreateDto dto = new ReviewCreateDto(
                anotherMember.getId(),
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    @DisplayName("이미 리뷰를 작성했던 멘토링에 중복으로 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReviewFail3() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));

        ReviewCreateDto dto = new ReviewCreateDto(
                mentee.getId(),
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        reviewService.createReview(dto);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(ReviewAlreadyExistsException.class)
                .hasMessage(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
    }

    @DisplayName("멘토링이 완료되지 않은 예약에는 리뷰를 남길 수 없다")
    @Test
    void createReviewFail4() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestPendingReservation(mentoring, mentee));

        ReviewCreateDto dto = new ReviewCreateDto(
                mentee.getId(),
                reservation.getId(),
                5,
                "최고의 멘토링이었습니다."
        );

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(dto))
                .isInstanceOf(ReservationNotCompletedException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_NOT_COMPLETED.getMessage());
    }

    @DisplayName("특정 멤버의 리뷰를 모두 조회 성공 시 리뷰 정보를 반환한다")
    @Test
    void findMemberReviews() {
        // given
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Member mentor1 = em.persist(FixtureUtil.getTestMentor());
        Member mentor2 = em.persist(FixtureUtil.getTestMentor(2));

        Mentoring mentoring1 = em.persist(FixtureUtil.getTestMentoring(mentor1));
        Mentoring mentoring2 = em.persist(FixtureUtil.getTestMentoring(mentor2));

        Reservation reservation1 = em.persist(FixtureUtil.getTestCompletedReservation(mentoring1, mentee));
        Reservation reservation2 = em.persist(FixtureUtil.getTestCompletedReservation(mentoring2, mentee));

        Review review1 = em.persist(FixtureUtil.getTestReview(reservation1, mentee));
        Review review2 = em.persist(FixtureUtil.getTestReview(reservation2, mentee));

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
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));

        Member mentee1 = em.persist(FixtureUtil.getTestMentee(1));
        Member mentee2 = em.persist(FixtureUtil.getTestMentee(2));

        Reservation reservation1 = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee1));
        Reservation reservation2 = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee1));
        Reservation reservation3 = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee2));
        Reservation reservation4 = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee2));

        Review review1 = insertReviewUsingNativeQuery(
                2, "최고의 멘토링이었습니다.",
                LocalDateTime.of(2025, 9, 1, 10, 0, 0),
                reservation1, mentee1
        );
        Review review2 = insertReviewUsingNativeQuery(
                2, "최고의 멘토링이었습니다.",
                LocalDateTime.of(2025, 9, 2, 9, 0, 0),
                reservation2, mentee1
        );
        Review review3 = insertReviewUsingNativeQuery(
                2, "최고의 멘토링이었습니다.",
                LocalDateTime.of(2025, 9, 3, 10, 0, 0),
                reservation3, mentee2
        );
        Review review4 = insertReviewUsingNativeQuery(
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
                            review3.getId(),
                            review3.getMenteeName(),
                            review3.getCreatedAt().toLocalDate(),
                            review3.getRating(),
                            review3.getContent()
                    ),
                    new ReviewGetResponse(
                            review4.getId(),
                            review4.getMenteeName(),
                            review4.getCreatedAt().toLocalDate(),
                            review4.getRating(),
                            review4.getContent()
                    ),
                    new ReviewGetResponse(
                            review2.getId(),
                            review2.getMenteeName(),
                            review2.getCreatedAt().toLocalDate(),
                            review2.getRating(),
                            review2.getContent()
                    ),
                    new ReviewGetResponse(
                            review1.getId(),
                            review1.getMenteeName(),
                            review1.getCreatedAt().toLocalDate(),
                            review1.getRating(),
                            review1.getContent()
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
        em.getEntityManager().createNativeQuery("""
                        INSERT INTO review (
                            rating, content, created_at, is_deleted, deleted_at, reservation_id, mentee_id
                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                        """)
                .setParameter(1, rating)
                .setParameter(2, content)
                .setParameter(3, createdAt)
                .setParameter(4, false)
                .setParameter(5, null)
                .setParameter(6, reservation.getId())
                .setParameter(7, mentee.getId())
                .executeUpdate();

        Long insertedId = ((Number) em.getEntityManager()
                .createNativeQuery("SELECT LAST_INSERT_ID()")
                .getSingleResult())
                .longValue();

        return em.find(Review.class, insertedId);
    }

    @DisplayName("본인이 남긴 리뷰의 별점을 수정한다")
    @Test
    void modifyReview1() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));

        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));
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
        em.flush();
        em.clear();

        // then
        Review updated = em.find(Review.class, review.getId());
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
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));

        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));
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
        em.flush();
        em.clear();

        // then
        Review updated = em.find(Review.class, review.getId());
        assertSoftly(softly -> {
            softly.assertThat(updated.getRating()).isEqualTo(newRating);
            softly.assertThat(updated.getContent()).isEqualTo(originalContent);
        });
    }

    @DisplayName("본인이 남긴 리뷰의 내용을 수정한다")
    @Test
    void modifyReview3() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));

        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));
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
        em.flush();
        em.clear();

        // then
        Review updated = em.find(Review.class, review.getId());
        assertSoftly(softly -> {
            softly.assertThat(updated.getRating()).isEqualTo(originalRating);
            softly.assertThat(updated.getContent()).isEqualTo(newContent);
        });
    }

    @DisplayName("본인이 남긴 리뷰의 별점과 내용을 수정한다")
    @Test
    void modifyReview4() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));
        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));

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
        em.flush();
        em.clear();

        // then
        Review updated = em.find(Review.class, review.getId());
        assertSoftly(softly -> {
            softly.assertThat(updated.getRating()).isEqualTo(newRating);
            softly.assertThat(updated.getContent()).isEqualTo(newContent);
        });
    }

    @DisplayName("존재하지 않는 리뷰를 수정하려고 하면 예외가 발생한다")
    @Test
    void modifyReviewFail1() {
        // given
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        ReviewModifyDto dto = new ReviewModifyDto(
                mentee.getId(),
                999L,
                2,
                "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when & then
        assertThatThrownBy(() -> reviewService.modifyReview(dto))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 수정하려고 하면 예외가 발생한다")
    @Test
    void modifyReviewFail2() {
        // given
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));
        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));

        // 리뷰 작성자가 아닌 다른 멤버
        Member invalidMember = em.persist(FixtureUtil.getTestMentee(2));

        ReviewModifyDto dto = new ReviewModifyDto(
                invalidMember.getId(),
                review.getId(),
                2,
                "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when & then
        assertThatThrownBy(() -> reviewService.modifyReview(dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.NOT_REVIEW_OWNER.getMessage());
    }

    @DisplayName("존재하지 않는 리뷰 삭제 요청 시 예외가 발생한다")
    @Test
    void deleteReviewFail1() {
        // given
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        ReviewDeleteDto dto = new ReviewDeleteDto(mentee.getId(), 999L); // 존재하지 않는 리뷰 ID

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(dto))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 삭제하려고 하면 예외가 발생한다")
    @Test
    void deleteReviewFail2() {
        // given
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));
        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));

        Member invalidMember = em.persist(FixtureUtil.getTestMentee(2));

        ReviewDeleteDto dto = new ReviewDeleteDto(invalidMember.getId(), review.getId());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.NOT_REVIEW_OWNER.getMessage());
    }

    @DisplayName("존재하지 않는 리뷰에 대해 삭제를 요청하면 예외가 발생한다.")
    @Test
    void failNotFoundReviewDelete() {
        // given
        Member admin = em.persist(FixtureUtil.getTestAdmin());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteForAdmin(admin.getId(), 1L))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("관리자 권한 없이 리뷰 삭제를 요청하면 예외가 발생한다.")
    @Test
    void failReviewDeleteWithoutAdmin() {
        // given
        Member notAdmin = em.persist(FixtureUtil.getTestMentee());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteForAdmin(notAdmin.getId(), 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    @DisplayName("관리자가 존재하는 리뷰에 대해 삭제를 요청하면 정상적으로 삭제한다.")
    @Test
    void successReviewDelete() {
        // given
        Member admin = em.persist(FixtureUtil.getTestAdmin());
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Member mentor = em.persist(FixtureUtil.getTestMentor());

        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));
        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));

        em.flush();
        em.clear();

        // when
        reviewService.deleteForAdmin(admin.getId(), review.getId());
        em.flush();
        em.clear();

        // then
        assertThat(reviewRepository.findById(review.getId())).isEmpty();
    }

    @DisplayName("리뷰를 삭제하면 삭제가 일어난 시간과 함께 삭제상태로 변경된다.")
    @Test
    void deleteReview() {
        // given
        Member mentee = em.persist(FixtureUtil.getTestMentee());
        Member mentor = em.persist(FixtureUtil.getTestMentor());
        Mentoring mentoring = em.persist(FixtureUtil.getTestMentoring(mentor));
        MentoringStatistics stats = em.persist(MentoringStatistics.defaultOf(mentoring));

        Reservation reservation = em.persist(FixtureUtil.getTestCompletedReservation(mentoring, mentee));
        Review review = em.persist(FixtureUtil.getTestReview(reservation, mentee));

        ReviewDeleteDto dto = new ReviewDeleteDto(mentee.getId(), review.getId());
        long originalReviewCount = stats.getReviewCount();
        long originalRatingSum = stats.getRatingSum();

        // when
        reviewService.deleteReview(dto);
        em.flush();
        em.clear();

        // then (소프트 삭제 확인을 위해 네이티브 조회)
        Review deletedReview = (Review) em.getEntityManager()
                .createNativeQuery("SELECT * FROM review WHERE id = ?", Review.class)
                .setParameter(1, review.getId())
                .getSingleResult();

        assertSoftly(softly -> {
            softly.assertThat(deletedReview.isDeleted()).isTrue();
            softly.assertThat(deletedReview.getDeletedAt()).isNotNull();
            softly.assertThat(mentoringStatisticsRepository.findById(mentoring.getId()).get().getReviewCount())
                    .isEqualTo(originalReviewCount - 1);
            softly.assertThat(mentoringStatisticsRepository.findById(mentoring.getId()).get().getRatingSum())
                    .isEqualTo(originalRatingSum - review.getRating());
        });
    }
}
