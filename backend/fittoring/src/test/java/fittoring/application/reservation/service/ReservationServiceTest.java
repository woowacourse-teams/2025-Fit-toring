package fittoring.application.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.SpringBootTestSupport;
import fittoring.admin.presentation.dto.AdminReservationDeleteDto;
import fittoring.admin.service.dto.AdminReservationStatusUpdateDto;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MentorAndMenteeIsSameException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.exception.ReservationNotFoundException;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CategoryMentoringRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.mentoring.service.dto.MentorMentoringReservationResponse;
import fittoring.application.reservation.presentation.dto.response.ParticipatedReservationResponse;
import fittoring.application.reservation.presentation.dto.response.PhoneNumberResponse;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.reservation.service.dto.ReservationCreateDto;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import java.util.List;
import java.util.TimeZone;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class ReservationServiceTest extends SpringBootTestSupport {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.setProperty("user.timezone", "Asia/Seoul");
    }

    @DisplayName("예약 생성이 성공하면 예약 객체를 반환하고, 예약 상태는 PENDING 상태이다.")
    @Test
    void createReservation() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        MentoringStatistics mentoringStatistics =
                mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));
        long originalReservationCount = mentoringStatistics.getReservationCount();

        ReservationCreateDto dto = new ReservationCreateDto(
                mentee.getId(),
                mentoring.getId(),
                "운동을 배우고 싶어요."
        );

        // when
        Reservation actual = reservationService.createReservation(dto);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual.getMentorName()).isEqualTo(mentor.getName());
            softly.assertThat(actual.getMenteeName()).isEqualTo(mentee.getName());
            softly.assertThat(actual.getMenteePhone()).isEqualTo(mentee.getPhoneNumber());
            softly.assertThat(actual.getContent()).isEqualTo(dto.content());
            softly.assertThat(actual.getStatus()).isEqualTo(Status.PENDING.name());
            softly.assertThat(
                    mentoringStatisticsRepository.findById(mentoring.getId()).get().getReservationCount()
            ).isEqualTo(originalReservationCount + 1);
        });
    }

    @DisplayName("본인이 개설한 멘토링에 예약하려고 하면 예외가 발생한다")
    @Test
    void createReservationFail1() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        ReservationCreateDto dto = new ReservationCreateDto(
                mentor.getId(),          // menteeId == mentorId → 동일인 예약 시도
                mentoring.getId(),
                "운동을 배우고 싶어요."
        );

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(dto))
                .isInstanceOf(MentorAndMenteeIsSameException.class)
                .hasMessage(BusinessErrorMessage.MENTOR_AND_MENTEE_IS_SAME.getMessage());
    }

    @DisplayName("존재하지 않는 멘토링이라면 예외가 발생한다.")
    @Test
    void createReservationFail2() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());
        long invalidMentoringId = 100L;

        ReservationCreateDto dto = new ReservationCreateDto(
                mentee.getId(),
                invalidMentoringId,
                "운동을 배우고 싶어요."
        );

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(dto))
                .isInstanceOf(MentoringNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
    }

    @DisplayName("특정 멘토가 개설한 멘토링의 모든 예약을 반환한다.")
    @Test
    void getAllReservationByMentor() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Member mentee1 = memberRepository.save(FixtureUtil.getTestMentee(1));
        Member mentee2 = memberRepository.save(FixtureUtil.getTestMentee(2));
        Member mentee3 = memberRepository.save(FixtureUtil.getTestMentee(3));

        reservationRepository.save(FixtureUtil.getTestPendingReservation(mentoring, mentee1));
        reservationRepository.save(FixtureUtil.getTestPendingReservation(mentoring, mentee2));
        reservationRepository.save(FixtureUtil.getTestPendingReservation(mentoring, mentee3));

        // when
        List<MentorMentoringReservationResponse> actual =
                reservationService.getReservationsByMentor(mentor.getId());

        // then
        assertThat(actual).hasSize(3);
    }

    @DisplayName("특정 멘토가 개설한 멘토링의 예약이 존재하지 않으면 빈 리스트를 반환한다.")
    @Test
    void getAllReservationByMentor2() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        // 예약 생성 없음!

        // when
        List<MentorMentoringReservationResponse> actual =
                reservationService.getReservationsByMentor(mentor.getId());

        // then
        assertThat(actual).isEmpty();
    }

    @DisplayName("예약의 상태를 변경할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "APPROVED, APPROVED",
            "REJECTED, REJECTED",
            "COMPLETE, COMPLETE"
    })
    void updateStatus(String requestStatus, String expectedStatusValue) {
        // given
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Reservation reservation = reservationRepository.save(
                new Reservation("content", Status.PENDING, mentoring, mentee)
        );

        // when
        reservationService.updateStatus(reservation.getId(), requestStatus);

        // then
        Reservation actual = reservationRepository.findById(reservation.getId())
                .orElse(null);
        assertThat(actual.getStatus()).isEqualTo(expectedStatusValue);
    }

    @DisplayName("예약자(멘티)의 전화번호를 반환할 수 있다.")
    @Test
    void getPhone() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(
                new Reservation("content", Status.PENDING, mentoring, mentee)
        );

        // when
        PhoneNumberResponse actual = reservationService.getPhone(reservation.getId());

        // then
        assertThat(actual.phoneNumber()).isEqualTo(mentee.getPhoneNumber());
    }

    @DisplayName("특정 멤버가 작성한 예약 조회에 성공하면 예약과 해당 예약이 달린 멘토링 정보를 반환한다")
    @Test
    void findMemberReservations() {
        // given
        Member mentor1 = memberRepository.save(FixtureUtil.getTestMentor(1));
        Member mentor2 = memberRepository.save(FixtureUtil.getTestMentor(2));

        Mentoring mentoring1 = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor1));
        Mentoring mentoring2 = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor2));

        // 멘토링1 프로필 이미지
        Image profileImageOfMentor1 = imageRepository.save(
                new Image("www.naver.com", ImageType.MENTORING_PROFILE, mentoring1.getId(), "baseName")
        );

        // 카테고리
        Category c1 = categoryRepository.save(new Category("근육 증진"));
        Category c2 = categoryRepository.save(new Category("다이어트"));
        Category c3 = categoryRepository.save(new Category("보디빌딩"));
        categoryMentoringRepository.save(new CategoryMentoring(c1, mentoring1));
        categoryMentoringRepository.save(new CategoryMentoring(c2, mentoring1));
        categoryMentoringRepository.save(new CategoryMentoring(c3, mentoring2));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation1 = reservationRepository.save(
                new Reservation("신청 내용1", Status.PENDING, mentoring1, mentee)
        );
        Reservation reservation2 = reservationRepository.save(
                new Reservation("신청 내용2", Status.PENDING, mentoring2, mentee)
        );

        // 리뷰는 두 번째 예약에만 달림 → expected의 마지막 boolean = true
        reviewRepository.save(new Review(4, "좋았습니다.", reservation2, mentee));

        List<ParticipatedReservationResponse> expected = List.of(
                new ParticipatedReservationResponse(
                        reservation1.getId(),
                        mentoring1.getId(),
                        mentoring1.getMentorName(),
                        profileImageOfMentor1.getUrl(),
                        reservation1.getCreatedAt().toLocalDate(),
                        reservation1.getContent(),
                        Status.PENDING.name(),
                        false
                ),
                new ParticipatedReservationResponse(
                        reservation2.getId(),
                        mentoring2.getId(),
                        mentoring2.getMentorName(),
                        null,
                        reservation2.getCreatedAt().toLocalDate(),
                        reservation2.getContent(),
                        Status.PENDING.name(),
                        true
                )
        );

        // when
        List<ParticipatedReservationResponse> actual =
                reservationService.findMemberReservations(mentee.getId());

        // then
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("관리자는 예약의 상태를 변경할 수 있다")
    @CsvSource({
            "PENDING, APPROVED",
            "PENDING, REJECTED",
            "PENDING, COMPLETE",
            "APPROVED, PENDING",
            "APPROVED, REJECTED",
            "APPROVED, COMPLETE",
            "REJECTED, PENDING",
            "REJECTED, APPROVED",
            "REJECTED, COMPLETE",
            "COMPLETE, PENDING",
            "COMPLETE, APPROVED",
            "COMPLETE, REJECTED"
    })
    @ParameterizedTest
    void updateStatusWithAdminAuthorization(Status originalStatus, String newStatus) {
        // given
        Member admin = memberRepository.save(FixtureUtil.getTestAdmin());
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(
                new Reservation("예약 내용", originalStatus, mentoring, mentee)
        );

        AdminReservationStatusUpdateDto dto =
                new AdminReservationStatusUpdateDto(admin.getId(), reservation.getId(), newStatus);

        // when
        reservationService.updateStatusWithAdminAuthorization(dto);

        // then
        Reservation actual = reservationRepository.findById(reservation.getId())
                .orElse(null);
        assertThat(actual.getStatus()).isEqualTo(newStatus);
    }

    @DisplayName("관리자는 등록되어 있는 예약을 삭제하면 삭제 상태로 변경되고, 연관된 리뷰도 함께 삭제 상태가 된다.")
    @Test
    void deleteReservationWithAdminAuthorization() {
        // given
        Member admin = memberRepository.save(FixtureUtil.getTestAdmin());
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());
        Reservation reservation = reservationRepository.save(FixtureUtil.getTestPendingReservation(mentoring, mentee));
        Review review = reviewRepository.save(FixtureUtil.getTestReview(reservation, mentee));
        MentoringStatistics stats = mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));
        long originalReservationCount = stats.getReservationCount();

        AdminReservationDeleteDto dto =
                new AdminReservationDeleteDto(admin.getId(), reservation.getId());

        // when
        reservationService.deleteReservationWithAdminAuthorization(dto);

        // then
        Boolean deletedReservation = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM reservation WHERE id = ?", Boolean.class, reservation.getId()
        );

        Boolean deletedReview = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM review WHERE id = ?", Boolean.class, review.getId()
        );

        assertSoftly(softly -> {
            softly.assertThat(deletedReservation).isTrue();
            softly.assertThat(deletedReview).isTrue();
            softly.assertThat(
                    mentoringStatisticsRepository.findById(mentoring.getId()).get().getReservationCount()
            ).isEqualTo(originalReservationCount - 1);
        });
    }

    @DisplayName("존재하지 않는 예약을 삭제하는 경우 예외가 발생한다.")
    @Test
    void deleteReservationWithAdminAuthorization2() {
        // given
        Member admin = memberRepository.save(FixtureUtil.getTestAdmin());
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        memberRepository.save(FixtureUtil.getTestMentee());

        Long invalidReservationId = 999L;
        AdminReservationDeleteDto dto =
                new AdminReservationDeleteDto(admin.getId(), invalidReservationId);

        // when & then
        assertThatThrownBy(() -> reservationService.deleteReservationWithAdminAuthorization(dto))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage());
    }
}
