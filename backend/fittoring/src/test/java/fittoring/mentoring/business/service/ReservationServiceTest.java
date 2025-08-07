package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.config.JpaConfiguration;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.service.dto.MentorMentoringReservationResponse;
import fittoring.mentoring.business.service.dto.ReservationCreateDto;
import fittoring.util.DbCleaner;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @DisplayName("예약 생성이 성공하면 예약 객체를 반환하고, 예약 상태는 PENDING 상태이다.")
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
        Reservation actual = reservationService.createReservation(dto);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
                    softAssertions.assertThat(actual.getMentorName()).isEqualTo(mentor.getName());
                    softAssertions.assertThat(actual.getMenteeName()).isEqualTo(mentee.getName());
                    softAssertions.assertThat(actual.getMenteePhone()).isEqualTo(mentee.getPhoneNumber());
                    softAssertions.assertThat(actual.getContext()).isEqualTo(dto.content());
                    softAssertions.assertThat(actual.getStatus()).isEqualTo(Status.PENDING.getValue());
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

    @DisplayName("특정 멘토가 개설한 멘토링의 모든 예약을 반환한다.")
    @Test
    void getAllReservationByMentor() {
        //given
        //멘토 등록
        Member mentor = new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw"));
        Member savedMentor = entityManager.persist(mentor);

        //멘티 생성
        Member mentee = new Member("id2", "MALE", "멘토1", new Phone("010-3455-5678"), Password.from("pw"));
        Member savedMentee = entityManager.persist(mentee);

        Member mentee2 = new Member("id3", "MALE", "멘토1", new Phone("010-5432-1234"), Password.from("pw"));
        Member savedMentee2 = entityManager.persist(mentee2);

        Member mentee3 = new Member("id4", "MALE", "멘토1", new Phone("010-8909-1234"), Password.from("pw"));
        Member savedMentee3 = entityManager.persist(mentee3);

        //멘토링 개설
        Mentoring mentoring = new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        );
        entityManager.persist(mentoring);

        //예약 생성
        Reservation reservation = new Reservation("context", mentoring, savedMentee, Status.PENDING);
        entityManager.persist(reservation);

        Reservation reservation2 = new Reservation("context", mentoring, savedMentee2, Status.PENDING);
        entityManager.persist(reservation2);

        Reservation reservation3 = new Reservation("context", mentoring, savedMentee3, Status.PENDING);
        entityManager.persist(reservation3);

        //when
        List<MentorMentoringReservationResponse> actual = reservationService.getReservationsByMentor(
                savedMentor.getId());

        //then
        assertThat(actual).hasSize(3);
    }

    @DisplayName("특정 멘토가 개설한 멘토링의 예약이 존재하지 않으면 빈 리스트를 반환한다.")
    @Test
    void getAllReservationByMentor2() {
        //given
        //멘토 생성
        Member mentor = new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw"));
        Member savedMentor = entityManager.persist(mentor);

        //멘토링 개설
        Mentoring mentoring = new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        );
        entityManager.persist(mentoring);

        //멘티 생성
        Member mentee = new Member("id2", "MALE", "멘토1", new Phone("010-3455-5678"), Password.from("pw"));
        Member savedMentee = entityManager.persist(mentee);

        Member mentee2 = new Member("id3", "MALE", "멘토1", new Phone("010-5432-1234"), Password.from("pw"));
        Member savedMentee2 = entityManager.persist(mentee2);

        Member mentee3 = new Member("id4", "MALE", "멘토1", new Phone("010-8909-1234"), Password.from("pw"));
        Member savedMentee3 = entityManager.persist(mentee3);

        //when
        List<MentorMentoringReservationResponse> actual = reservationService.getReservationsByMentor(
                savedMentor.getId());

        //then
        assertThat(actual).isEmpty();
    }

    @DisplayName("예약의 상태를 변경할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "APPROVE, 승인",
            "REJECT, 거절",
            "COMPLETE, 완료"
    })
    void updateStatus(String requestStatus, String expectedStatusValue) {
        //given
        Member mentor = new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw"));
        Member savedMentor = entityManager.persist(mentor);

        Mentoring mentoring = new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        );
        entityManager.persist(mentoring);

        Member mentee = new Member("id2", "MALE", "멘토1", new Phone("010-3455-5678"), Password.from("pw"));
        Member savedMentee = entityManager.persist(mentee);

        Reservation reservation = new Reservation("context", mentoring, savedMentee, Status.PENDING);
        Reservation savedReservation = entityManager.persist(reservation);

        //when
        reservationService.updateStatus(reservation.getId(), requestStatus);
        entityManager.flush();
        entityManager.clear();

        //then
        Reservation actual = entityManager.find(Reservation.class, savedReservation.getId());
        assertThat(actual.getStatus()).isEqualTo(expectedStatusValue);
    }
}
