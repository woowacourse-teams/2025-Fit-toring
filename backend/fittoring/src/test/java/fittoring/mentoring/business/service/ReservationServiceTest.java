package fittoring.mentoring.business.service;

import fittoring.config.JpaConfiguration;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.mentoring.presentation.dto.ReservationCreateResponse;
import fittoring.util.DbCleaner;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
    private EntityManager entityManager;

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
        String mentorName = "멘토";
        String menteeName = "멘티";
        String mentorPhone = "010-1234-5678";
        String menteePhone = "010-0123-9876";

        Mentoring mentoring = new Mentoring(
                mentorName,
                mentorPhone,
                5000,
                5,
                "구구절절한 내용",
                "한 줄 소개"
        );
        entityManager.persist(mentoring);
        entityManager.flush();
        ReservationCreateDto dto = new ReservationCreateDto(
                mentoring.getId(),
                menteeName,
                menteePhone,
                "운동을 배우고 싶어요."
        );

        // when
        ReservationCreateResponse reservationResponse = reservationService.createReservation(dto);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
                    softAssertions.assertThat(reservationResponse.menteeName()).isEqualTo(menteeName);
                    softAssertions.assertThat(reservationResponse.menteePhone()).isEqualTo(menteePhone);
                    softAssertions.assertThat(reservationResponse.mentorName()).isEqualTo(mentorName);
                    softAssertions.assertThat(reservationResponse.mentorPhone()).isEqualTo(mentorPhone);
                }
        );
    }
}