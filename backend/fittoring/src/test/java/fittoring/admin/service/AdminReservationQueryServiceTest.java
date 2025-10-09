package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.dto.AdminMentoringReservationDto;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.util.DbCleaner;
import java.time.temporal.ChronoUnit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({
        DbCleaner.class,
        AdminMemberQueryService.class,
        AdminReservationQueryService.class,
        JpaConfiguration.class,
        QueryDslConfig.class
})
@DataJpaTest
class AdminReservationQueryServiceTest {

    @MockitoBean
    private MentoringPaginationHelper mentoringPaginationHelper;

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

    @DisplayName("멘토링 별 예약 목록 조회")
    @Nested
    class MentoringReservations {

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

            entityManager.flush();
            entityManager.refresh(reservation1);
            entityManager.refresh(reservation2);

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
            Assertions.assertThat(actual.content())
                    .extracting(
                            AdminReservationResponse::reservationId,
                            AdminReservationResponse::menteeName,
                            r -> r.createdAt().truncatedTo(ChronoUnit.SECONDS),
                            AdminReservationResponse::status,
                            AdminReservationResponse::content
                    )
                    .containsExactly(
                            AssertionsForClassTypes.tuple(
                                    reservation2.getId(),
                                    reservation2.getMenteeName(),
                                    reservation2.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
                                    reservation2.getOriginalStatus(),
                                    reservation2.getContent()
                            ),
                            AssertionsForClassTypes.tuple(
                                    reservation1.getId(),
                                    reservation1.getMenteeName(),
                                    reservation1.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
                                    reservation1.getOriginalStatus(),
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
}
