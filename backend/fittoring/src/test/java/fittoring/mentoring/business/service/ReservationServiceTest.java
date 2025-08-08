package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.config.JpaConfiguration;
import fittoring.config.S3Configuration;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Review;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.infra.S3Uploader;
import fittoring.mentoring.presentation.dto.ParticipatedReservationResponse;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import fittoring.util.DbCleaner;
import java.util.List;
import org.assertj.core.api.Assertions;
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
@Import({DbCleaner.class, ReservationService.class, JpaConfiguration.class, ImageService.class, S3Uploader.class, S3Configuration.class})
@DataJpaTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("예약 생성이 성공하면 예약 정보를 반환한다.")
    @Test
    void createReservation() {
        // given
        Member mentee = new Member("id1", "남", "멘티남", new Phone("010-1234-5678"), Password.from("pw"));
        Member mentor = new Member("id2", "녀", "멘토녀", new Phone("010-1234-5679"), Password.from("pw2"));
        entityManager.persist(mentee);
        entityManager.persist(mentor);
        Mentoring mentoring = new Mentoring(
                mentor,
                5000,
                5,
                "구구절절한 내용",
                "한 줄 소개"
        );
        entityManager.persist(mentoring);
        ReservationCreateDto dto = new ReservationCreateDto(
                mentee.getId(),
                mentoring.getId(),
                "운동을 배우고 싶어요."
        );

        // when
        ReservationCreateResponse reservationResponse = reservationService.createReservation(dto);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
                    softAssertions.assertThat(reservationResponse.menteeName()).isEqualTo(mentee.getName());
                    softAssertions.assertThat(reservationResponse.menteePhone()).isEqualTo(mentee.getPhoneNumber());
                    softAssertions.assertThat(reservationResponse.mentorName()).isEqualTo(mentor.getName());
                    softAssertions.assertThat(reservationResponse.mentorPhone()).isEqualTo(mentor.getPhoneNumber());
                }
        );
    }

    @DisplayName("존재하지 않는 멘토링이라면 예외가 발생한다.")
    @Test
    void createReservationFail() {
        // given
        Member mentee = new Member("id1", "남", "멘티남", new Phone("010-1234-5678"), Password.from("pw"));
        entityManager.persist(mentee);
        long invalidMentoringId = 100L;
        ReservationCreateDto dto = new ReservationCreateDto(
                mentee.getId(),
                invalidMentoringId,
                "운동을 배우고 싶어요."
        );

        // when
        // then
        Assertions.assertThatThrownBy(() -> reservationService.createReservation(dto))
                .isInstanceOf(MentoringNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
    }

    @DisplayName("특정 멤버가 작성한 예약 조회에 성공하면 예약과 해당 예약이 달린 멘토링 정보를 반환한다")
    @Test
    void findMemberReservations() {
        // given
        Member mentor1 = entityManager.persist(new Member(
            "mentorId1",
            "남",
            "김멘토",
            new Phone("010-1234-5678"),
            Password.from("password")
        ));
        Image profileImageOfMentor1 = entityManager.persist(new Image(
            "www.naver.com",
            ImageType.MENTORING_PROFILE,
            mentor1.getId()
        ));
        Member mentor2 = entityManager.persist(new Member(
            "mentorId2",
            "남",
            "박멘토",
            new Phone("010-1234-5679"),
            Password.from("password")
        ));
        Mentoring mentoring1 = entityManager.persist(new Mentoring(
            mentor1,
            5_000,
            5,
            "한 줄 소개",
            "긴 글 소개"
        ));
        Mentoring mentoring2 = entityManager.persist(new Mentoring(
            mentor2,
            5_000,
            5,
            "한 줄 소개",
            "긴 글 소개"
        ));
        Category category1 = entityManager.persist(new Category("근육 증진"));
        Category category2 = entityManager.persist(new Category("다이어트"));
        Category category3 = entityManager.persist(new Category("보디빌딩"));
        CategoryMentoring category1OfMentoring1 = entityManager.persist(new CategoryMentoring(
            category1,
            mentoring1
        ));
        CategoryMentoring category2OfMentoring1 = entityManager.persist(new CategoryMentoring(
            category2,
            mentoring1
        ));
        CategoryMentoring category1OfMentoring2 = entityManager.persist(new CategoryMentoring(
            category3,
            mentoring2
        ));
        Member mentee = entityManager.persist(new Member(
            "menteeId",
            "남",
            "김멘티",
            new Phone("010-5678-1234"),
            Password.from("password")
        ));
        Reservation reservation1 = entityManager.persist(new Reservation(
            "신청 내용1",
            mentoring1,
            mentee
            ));
        Reservation reservation2 = entityManager.persist(new Reservation(
            "신청 내용2",
            mentoring2,
            mentee
            ));
        entityManager.persist(new Review(
            4,
            "좋았습니다.",
            reservation2,
            mentee
        ));
        List<ParticipatedReservationResponse> expected = List.of(
            new ParticipatedReservationResponse(
                reservation1.getId(),
                mentoring1.getId(),
                mentoring1.getMentorName(),
                profileImageOfMentor1.getUrl(),
                mentoring1.getPrice(),
                reservation1.getCreatedAt().toLocalDate(),
                List.of(category1OfMentoring1.getCategoryTitle(), category2OfMentoring1.getCategoryTitle()),
                false
            ),
            new ParticipatedReservationResponse(
                reservation2.getId(),
                mentoring2.getId(),
                mentoring2.getMentorName(),
                null,
                mentoring2.getPrice(),
                reservation2.getCreatedAt().toLocalDate(),
                List.of(category1OfMentoring2.getCategoryTitle()),
                true
            )
        );

        // when
        // then
        assertThat(reservationService.findMemberReservations(mentee.getId())).isEqualTo(expected);
    }
}
