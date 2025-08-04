package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.ReviewAlreadyExistsException;
import fittoring.mentoring.business.exception.ReviewNotFoundException;
import fittoring.mentoring.business.exception.ReviewerNotSameException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.service.dto.MemberReviewGetDto;
import fittoring.mentoring.business.service.dto.MentoringReviewGetDto;
import fittoring.mentoring.business.service.dto.ReviewCreateDto;
import fittoring.mentoring.business.service.dto.ReviewDeleteDto;
import fittoring.mentoring.business.service.dto.ReviewModifyDto;
import fittoring.mentoring.presentation.dto.MemberReviewGetResponse;
import fittoring.mentoring.presentation.dto.MentoringReviewGetResponse;
import fittoring.mentoring.presentation.dto.ReviewCreateResponse;
import fittoring.util.DbCleaner;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, ReviewService.class})
@DataJpaTest
class ReviewServiceTest {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private DbCleaner dbCleaner;
    
    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }
    
    @DisplayName("리뷰 작성을 성공하면 별점과 리뷰 내용을 반환한다")
    @Test
    void createReservation() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id 넣어주기
        int rating = 5;
        String content = "최고의 멘토링이었습니다.";
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            reviewer.getId(),
            mentoring.getId(),
            rating,
            content
        );
        
        // when
        ReviewCreateResponse reviewCreateResponse = reviewService.createReview(reviewCreateDto);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(reviewCreateResponse.rating()).isEqualTo(rating);
            softAssertions.assertThat(reviewCreateResponse.rating()).isEqualTo(content);
        });
    }
    
    @DisplayName("존재하지 않는 멘토링이라면 예외가 발생한다.")
    @Test
    void createReservationFail1() {
        // given
        Mentoring mentoring = entityManager.persist(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            999L,
            mentoring.getId(),
            5,
            "최고의 멘토링이었습니다."
        );
        
        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateDto))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessage(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("존재하지 않는 멤버의 요청이라면 예외가 발생한다.")
    @Test
    void createReservationFail2() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            reviewer.getId(),
            999L,
            5,
            "최고의 멘토링이었습니다."
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateDto))
            .isInstanceOf(MentoringNotFoundException.class)
            .hasMessage(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
    }

    @DisplayName("신청하지 않았던 멘토링에 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReservationFail3() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        int rating = 5;
        String content = "최고의 멘토링이었습니다.";
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            reviewer.getId(),
            mentoring.getId(),
            rating,
            content
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateDto))
            .isInstanceOf(ReservationNotFoundException.class)
            .hasMessage(BusinessErrorMessage.REVIEWING_RESERVATION_NOT_FOUND.getMessage());
    }

    @DisplayName("이미 리뷰를 작성했던 멘토링에 중복으로 리뷰 작성을 요청하면 예외가 발생한다")
    @Test
    void createReservationFail4() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id 넣어주기
        int rating = 5;
        String content = "최고의 멘토링이었습니다.";
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(
            reviewer.getId(),
            mentoring.getId(),
            rating,
            content
        );
        reviewService.createReview(reviewCreateDto);

        // when
        // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateDto))
            .isInstanceOf(ReviewAlreadyExistsException.class)
            .hasMessage(BusinessErrorMessage.DUPLICATED_REVIEW.getMessage());
    }

    @DisplayName("특정 멤버의 리뷰를 모두 조회 성공 시 리뷰 정보를 반환한다")
    @Test
    void findMemberReviews() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring1 = entityManager.persist(new Mentoring(
            "mentorName1",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        Mentoring mentoring2 = entityManager.persist(new Mentoring(
            "mentorName2",
            "010-5678-1235",
            5000,
            5,
            "content",
            "introduction"
        ));
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id 넣어주기
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id2 넣어주기
        Review review1 = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            mentoring1,
            reviewer
        ));
        Review review2 = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            mentoring2,
            reviewer
        ));
        MemberReviewGetDto memberReviewGetDto = new MemberReviewGetDto(reviewer.getId());

        // when
        List<MemberReviewGetResponse> memberReviewGetResponses
            = reviewService.findMemberReviews(memberReviewGetDto);

        // then
        assertThat(memberReviewGetResponses).containsExactlyInAnyOrder(
            new MemberReviewGetResponse(
                review1.getId(),
                review1.getMentoring().getId(),
                review1.getRating(),
                review1.getContent()
            ),
            new MemberReviewGetResponse(
                review2.getId(),
                review2.getMentoring().getId(),
                review2.getRating(),
                review2.getContent()
            )
        );
    }

    @DisplayName("특정 멘토링에 달린 리뷰 조회 성공 시 리뷰 정보를 반환한다")
    @Test
    void findMentoringReviews() {
        // given
        Mentoring mentoring = entityManager.persist(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        Member reviewer1 = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Member reviewer2 = entityManager.persist(new Member(
            "loginId2",
            "남",
            "name",
            "010-1234-5679",
            "password"
        ));
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id 넣어주기
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id 넣어주기
        Review review1 = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            mentoring,
            reviewer1
        ));
        Review review2 = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            mentoring,
            reviewer2
        ));
        MentoringReviewGetDto mentoringReviewGetDto = new MentoringReviewGetDto(mentoring.getId());

        // when
        List<MentoringReviewGetResponse> mentoringReviewGetResponses
            = reviewService.findMentoringReviews(mentoringReviewGetDto);

        // then
        assertThat(mentoringReviewGetResponses).containsExactlyInAnyOrder(
            new MentoringReviewGetResponse(
                review1.getId(),
                review1.getReviewer().getName(),
                review1.getRating(),
                review1.getContent()
            ),
            new MentoringReviewGetResponse(
                review2.getId(),
                review1.getReviewer().getName(),
                review2.getRating(),
                review2.getContent()
            )
        );
    }

    @DisplayName("존재하지 않는 리뷰를 수정하려고 하면 예외가 발생한다")
    @Test
    void modifyReviewFail1() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        ReviewModifyDto reviewModifyDto = new ReviewModifyDto(
            reviewer.getId(),
            999L,
            2,
            "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.modifyReview(reviewModifyDto))
            .isInstanceOf(ReviewNotFoundException.class)
            .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 수정하려고 하면 예외가 발생한다")
    @Test
    void modifyReviewFail2() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id 넣어주기
        Review review = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            mentoring,
            reviewer
        ));
        Member invalidMember = entityManager.persist(new Member(
            "loginId2",
            "남",
            "name2",
            "010-1234-5679",
            "password"
        ));
        ReviewModifyDto reviewModifyDto = new ReviewModifyDto(
            invalidMember.getId(),
            review.getId(),
            2,
            "생각해 보니 비용이 너무 비쌌던 것 같아요"
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.modifyReview(reviewModifyDto))
            .isInstanceOf(ReviewerNotSameException.class)
            .hasMessage(BusinessErrorMessage.REVIEWER_NOT_SAME.getMessage());
    }

    @DisplayName("존재하지 않는 리뷰 삭제 요청 시 예외가 발생한다")
    @Test
    void deleteReviewFail1() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        ReviewDeleteDto reviewDeleteDto = new ReviewDeleteDto(reviewer.getId(), 999L);

        // when
        // then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewDeleteDto))
            .isInstanceOf(ReviewNotFoundException.class)
            .hasMessage(BusinessErrorMessage.REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("본인이 작성하지 않은 리뷰를 삭제하려고 하면 예외가 발생한다")
    @Test
    void deleteReviewFail2() {
        // given
        Member reviewer = entityManager.persist(new Member(
            "loginId",
            "남",
            "name",
            "010-1234-5678",
            "password"
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
            "mentorName",
            "010-5678-1234",
            5000,
            5,
            "content",
            "introduction"
        ));
        entityManager.persist(new Reservation()); // TODO: 멘토링 개설 되면 id 넣어주기
        Review review = entityManager.persist(new Review(
            5,
            "최고의 멘토링이었습니다.",
            mentoring,
            reviewer
        ));
        Member invalidMember = entityManager.persist(new Member(
            "loginId2",
            "남",
            "name2",
            "010-1234-5679",
            "password"
        ));
        ReviewDeleteDto reviewDeleteDto = new ReviewDeleteDto(
            invalidMember.getId(),
            review.getId()
        );

        // when
        // then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewDeleteDto))
            .isInstanceOf(ReviewerNotSameException.class)
            .hasMessage(BusinessErrorMessage.REVIEWER_NOT_SAME.getMessage());
    }
}
