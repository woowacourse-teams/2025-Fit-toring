package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.dto.AdminMentoringReservationDto;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.member.service.MemberService;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.util.DbCleaner;
import org.assertj.core.api.Assertions;
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
@Import({
        DbCleaner.class,
        AdminMemberQueryService.class,
        QueryDslConfig.class
})
@DataJpaTest
class AdminReservationQueryServiceTest {

    @Autowired
    private AdminReservationQueryService reservationService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("관리자는 특정 멘토링에 달린 모든 예약을 조회할 수 있다")
    @Test
    void findMentoringReservationsWithAdminAuthorization() {
        // given
        Member admin = entityManager.persist(FixtureUtil.getTestAdmin());
        Member mentor = entityManager.persist(FixtureUtil.getTestMentor());
        Mentoring mentoring = entityManager.persist(FixtureUtil.getTestMentoring(mentor));

        Member mentee1 = entityManager.persist(FixtureUtil.getTestMentee(1));
        Member mentee2 = entityManager.persist(FixtureUtil.getTestMentee(2));

        Reservation reservation1 = entityManager.persist(FixtureUtil.getTestPendingReservation(mentoring, mentee1));
        Reservation reservation2 = entityManager.persist(FixtureUtil.getTestPendingReservation(mentoring, mentee2));

        AdminMentoringReservationDto dto = new AdminMentoringReservationDto(
                admin.getId(),
                mentoring.getId(),
                1,
                20
        );

        // when
        PageResult<AdminReservationResponse> actual =
                reservationService.findMentoringReservationsForAdmin(dto);

        // then
        Assertions.assertThat(actual.content()).containsExactly(
                new AdminReservationResponse(
                        reservation2.getId(),
                        reservation2.getMenteeName(),
                        reservation2.getCreatedAt(),
                        reservation2.getStatus(),
                        reservation2.getContent()
                ),
                new AdminReservationResponse(
                        reservation1.getId(),
                        reservation1.getMenteeName(),
                        reservation1.getCreatedAt(),
                        reservation1.getStatus(),
                        reservation1.getContent()
                )
        );
    }

    @DisplayName("관리자가 아닌 회원은 관리자용 예약 조회 기능을 사용할 수 없다")
    @Test
    void findMentoringReservationsWithAdminAuthorizationFail() {
        // given
        Member normalMember = entityManager.persist(FixtureUtil.getTestMentee());     // 비관리자
        Member mentor = entityManager.persist(FixtureUtil.getTestMentor());
        Mentoring mentoring = entityManager.persist(FixtureUtil.getTestMentoring(mentor));
        Member mentee1 = entityManager.persist(FixtureUtil.getTestMentee(1));
        Member mentee2 = entityManager.persist(FixtureUtil.getTestMentee(2));

        entityManager.persist(FixtureUtil.getTestPendingReservation(mentoring, mentee1));
        entityManager.persist(FixtureUtil.getTestPendingReservation(mentoring, mentee2));

        AdminMentoringReservationDto dto = new AdminMentoringReservationDto(
                normalMember.getId(),
                mentoring.getId(),
                1,
                20
        );

        // when & then
        Assertions.assertThatThrownBy(() -> reservationService.findMentoringReservationsForAdmin(dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }
}