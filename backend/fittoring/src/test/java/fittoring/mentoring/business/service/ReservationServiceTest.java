package fittoring.mentoring.business.service;

import fittoring.config.JpaConfiguration;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import fittoring.util.DbCleaner;
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
@Import({DbCleaner.class, ReservationService.class, JpaConfiguration.class})
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
}
