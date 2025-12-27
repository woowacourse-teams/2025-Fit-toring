package fittoring.admin.service;

import fittoring.IntegrationTestSupport;
import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.dto.AdminMentoringReservationDto;
import fittoring.application.FixtureUtil;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminReservationQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private AdminReservationQueryService reservationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("멘토링 별 예약 목록 조회")
    @Nested
    class MentoringReservations {

        @DisplayName("관리자는 특정 멘토링에 달린 모든 예약을 조회할 수 있다")
        @Test
        void findMentoringReservationsWithAdminAuthorization() {
            // given
            Member admin = memberRepository.save(FixtureUtil.getTestAdmin());
            Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
            Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

            Member mentee1 = memberRepository.save(FixtureUtil.getTestMentee(1));
            Member mentee2 = memberRepository.save(FixtureUtil.getTestMentee(2));

            Reservation reservation1 = reservationRepository.save(
                    FixtureUtil.getTestPendingReservation(mentoring, mentee1));
            Reservation reservation2 = reservationRepository.save(
                    FixtureUtil.getTestPendingReservation(mentoring, mentee2));

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
                            AdminReservationResponse::createdAt,
                            AdminReservationResponse::status,
                            AdminReservationResponse::content
                    )
                    .containsExactly(
                            AssertionsForClassTypes.tuple(
                                    reservation2.getId(),
                                    reservation2.getMenteeName(),
                                    reservation2.getCreatedAt(),
                                    reservation2.getOriginalStatus(),
                                    reservation2.getContent()
                            ),
                            AssertionsForClassTypes.tuple(
                                    reservation1.getId(),
                                    reservation1.getMenteeName(),
                                    reservation1.getCreatedAt(),
                                    reservation1.getOriginalStatus(),
                                    reservation1.getContent()
                            )
                    );
        }
    }
}
