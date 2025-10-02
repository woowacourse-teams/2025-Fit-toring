package fittoring.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MentorAndMenteeIsSameException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.exception.ReservationNotFoundException;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import fittoring.application.repository.MentoringStatisticsRepository;
import fittoring.application.repository.helper.MentoringPaginationHelper;
import fittoring.application.service.dto.AdminReservationStatusUpdateDto;
import fittoring.application.service.dto.MentorMentoringReservationResponse;
import fittoring.application.service.dto.MentoringReservationGetDto;
import fittoring.application.service.dto.PhoneNumberResponse;
import fittoring.application.service.dto.ReservationCreateDto;
import fittoring.infrastructure.image.ImageResizer;
import fittoring.infrastructure.image.ImageTranscoder;
import fittoring.infrastructure.image.S3Uploader;
import fittoring.infrastructure.image.policy.CertificatePolicy;
import fittoring.infrastructure.image.policy.ImagePolicyRegistry;
import fittoring.infrastructure.image.policy.MentoringProfilePolicy;
import fittoring.infrastructure.image.policy.NonePolicy;
import fittoring.admin.presentation.dto.AdminReservationDeleteDto;
import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.application.presentation.dto.ParticipatedReservationResponse;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({
        DbCleaner.class,
        JpaConfiguration.class,
        ImagePolicyRegistry.class,
        ImageResizer.class,
        ImageTranscoder.class,
        CertificatePolicy.class,
        MentoringProfilePolicy.class,
        NonePolicy.class,
        ReservationService.class,
        ImageService.class,
        QueryDslConfig.class,
        MentoringPaginationHelper.class
})
@DataJpaTest
class ReservationServiceTest {

    @MockitoBean
    private S3Uploader s3Uploader;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

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
                "한 줄 소개",
                "가상의오픈채팅링크"
        );
        entityManager.persist(mentoring);
        MentoringStatistics mentoringStatistics = entityManager.persist(MentoringStatistics.defaultOf(mentoring));
        long originalReservationCount = mentoringStatistics.getReservationCount();

        entityManager.flush();
        entityManager.clear();

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
                    softAssertions.assertThat(actual.getContent()).isEqualTo(dto.content());
                    softAssertions.assertThat(actual.getStatus()).isEqualTo(Status.PENDING.name());
                    softAssertions.assertThat(
                                    mentoringStatisticsRepository.findById(mentoring.getId()).get().getReservationCount())
                            .isEqualTo(originalReservationCount + 1);
                }
        );
    }

    @DisplayName("본인이 개설한 멘토링에 예약하려고 하면 예외가 발생한다")
    @Test
    void createReservationFail1() {
        // given
        Member mentor = entityManager.persist(new Member(
                "mentorLoginId",
                "MALE",
                "아이유",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
                mentor,
                5000,
                5,
                "모던 타임즈",
                "또 봐요 미스터 채플린~",
                "가상의오픈채팅링크"
        ));
        ReservationCreateDto dto = new ReservationCreateDto(
                mentor.getId(),
                mentoring.getId(),
                "그 이름도 내겐 사랑스런 채플린~"
        );

        // when
        // then
        assertThatThrownBy(() -> reservationService.createReservation(dto))
                .isInstanceOf(MentorAndMenteeIsSameException.class)
                .hasMessage(BusinessErrorMessage.MENTOR_AND_MENTEE_IS_SAME.getMessage());
    }

    @DisplayName("존재하지 않는 멘토링이라면 예외가 발생한다.")
    @Test
    void createReservationFail2() {
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
                "introduction",
                "가상의오픈채팅링크"
        );
        entityManager.persist(mentoring);

        //예약 생성
        Reservation reservation = new Reservation("content", Status.PENDING, mentoring, savedMentee);
        entityManager.persist(reservation);

        Reservation reservation2 = new Reservation("content", Status.PENDING, mentoring, savedMentee2);
        entityManager.persist(reservation2);

        Reservation reservation3 = new Reservation("content", Status.PENDING, mentoring, savedMentee3);
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
                "introduction",
                "가상의오픈채팅링크"
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
            "APPROVED, APPROVED",
            "REJECTED, REJECTED",
            "COMPLETE, COMPLETE"
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
                "introduction",
                "가상의오픈채팅링크"
        );
        entityManager.persist(mentoring);

        Member mentee = new Member("id2", "MALE", "멘토1", new Phone("010-3455-5678"), Password.from("pw"));
        Member savedMentee = entityManager.persist(mentee);

        Reservation reservation = new Reservation("content", Status.PENDING, mentoring, savedMentee);
        Reservation savedReservation = entityManager.persist(reservation);

        //when
        reservationService.updateStatus(reservation.getId(), requestStatus);
        entityManager.flush();
        entityManager.clear();

        //then
        Reservation actual = entityManager.find(Reservation.class, savedReservation.getId());
        assertThat(actual.getStatus()).isEqualTo(expectedStatusValue);
    }

    @DisplayName("예약자(멘티)의 전화번호를 반환할 수 있다.")
    @Test
    void getPhone() {
        //given
        Member mentor = new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw"));
        Member savedMentor = entityManager.persist(mentor);

        Mentoring mentoring = new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction",
                "가상의오픈채팅링크"
        );
        entityManager.persist(mentoring);

        Member mentee = new Member("id2", "MALE", "멘토1", new Phone("010-3455-5678"), Password.from("pw"));
        Member savedMentee = entityManager.persist(mentee);

        Reservation reservation = new Reservation("content", Status.PENDING, mentoring, savedMentee);
        Reservation savedReservation = entityManager.persist(reservation);
        entityManager.clear();

        //when
        PhoneNumberResponse actual = reservationService.getPhone(savedReservation.getId());

        //then
        assertThat(actual.phoneNumber()).isEqualTo(savedMentee.getPhoneNumber());
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
                "긴 글 소개",
                "가상의오픈채팅링크"
        ));
        Mentoring mentoring2 = entityManager.persist(new Mentoring(
                mentor2,
                5_000,
                5,
                "한 줄 소개",
                "긴 글 소개",
                "가상의오픈채팅링크"
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
                "mentorId",
                "남",
                "김멘티",
                new Phone("010-5678-1234"),
                Password.from("password")
        ));
        Reservation reservation1 = entityManager.persist(new Reservation(
                "신청 내용1",
                Status.PENDING,
                mentoring1,
                mentee

        ));
        Reservation reservation2 = entityManager.persist(new Reservation(
                "신청 내용2",
                Status.PENDING,
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

    @DisplayName("관리자는 특정 멘토링에 달린 모든 예약을 조회할 수 있다")
    @Test
    void findMentoringReservationsWithAdminAuthorization() {
        // given
        Member admin = entityManager.persist(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        Member mentor = entityManager.persist(new Member(
                "mentorId",
                "MALE",
                "아이유",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
                mentor,
                5000,
                5,
                "Last Fantasy",
                "아직 모르는 게 많은 나 저 문을 열고 걸어 나가도 되겠죠 날 천천히 기다릴 수 있나요 기도해줘요 넘어지지 않도록",
                "가상의오픈채팅링크"
        ));
        Member mentee1 = entityManager.persist(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Member mentee2 = entityManager.persist(new Member(
                "menteeId2",
                "MALE",
                "박멘티",
                new Phone("010-1234-5670"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation1 = entityManager.persist(new Reservation(
                "아득한 건 언제나 늘 아름답게 보이죠",
                Status.PENDING,
                mentoring,
                mentee1
        ));
        Reservation reservation2 = entityManager.persist(new Reservation(
                "가까이 다가 선 세상은 내게 뭘 보여 줄까요~",
                Status.COMPLETE,
                mentoring,
                mentee2
        ));
        MentoringReservationGetDto mentoringReservationGetDto = new MentoringReservationGetDto(
                admin.getId(),
                mentoring.getId()
        );

        // when
        List<AdminReservationResponse> responseBody = reservationService.findMentoringReservationsWithAdminAuthorization(
                mentoringReservationGetDto);

        // then
        assertThat(responseBody).containsExactlyInAnyOrder(
                new AdminReservationResponse(
                        reservation1.getId(),
                        reservation1.getMenteeName(),
                        reservation1.getCreatedAt().toLocalDate(),
                        reservation1.getStatus(),
                        reservation1.getContent()
                ),
                new AdminReservationResponse(
                        reservation2.getId(),
                        reservation2.getMenteeName(),
                        reservation2.getCreatedAt().toLocalDate(),
                        reservation2.getStatus(),
                        reservation2.getContent()
                )
        );
    }

    @DisplayName("관리자가 아닌 회원은 관리자용 예약 조회 기능을 사용할 수 없다")
    @Test
    void findMentoringReservationsWithAdminAuthorizationFail() {
        // given
        Member normalMember = entityManager.persist(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Member mentor = entityManager.persist(new Member(
                "mentorId",
                "MALE",
                "아이유",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
                mentor,
                5000,
                5,
                "Last Fantasy",
                "아직 모르는 게 많은 나 저 문을 열고 걸어 나가도 되겠죠 날 천천히 기다릴 수 있나요 기도해줘요 넘어지지 않도록",
                "가상의오픈채팅링크"
        ));
        Member mentee1 = entityManager.persist(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Member mentee2 = entityManager.persist(new Member(
                "menteeId2",
                "MALE",
                "박멘티",
                new Phone("010-1234-5670"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation1 = entityManager.persist(new Reservation(
                "아득한 건 언제나 늘 아름답게 보이죠",
                Status.PENDING,
                mentoring,
                mentee1
        ));
        Reservation reservation2 = entityManager.persist(new Reservation(
                "가까이 다가 선 세상은 내게 뭘 보여 줄까요~",
                Status.COMPLETE,
                mentoring,
                mentee2
        ));
        MentoringReservationGetDto mentoringReservationGetDto = new MentoringReservationGetDto(
                normalMember.getId(),
                mentoring.getId()
        );

        // when
        // that
        assertThatThrownBy(() -> reservationService.findMentoringReservationsWithAdminAuthorization(
                mentoringReservationGetDto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
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
        Member admin = entityManager.persist(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        Member mentor = entityManager.persist(new Member(
                "mentorId",
                "MALE",
                "고윤하",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
                mentor,
                5000,
                5,
                "살별",
                "이 비행의 끝에는 분명 너의 소원이 될 거라고 작은 목소리로 우리의 추억을 빌어볼게",
                "가상의오픈채팅링크"
        ));
        Member mentee = entityManager.persist(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation = entityManager.persist(new Reservation(
                "아득한 건 언제나 늘 아름답게 보이죠",
                originalStatus,
                mentoring,
                mentee
        ));
        AdminReservationStatusUpdateDto adminReservationStatusUpdateDto
                = new AdminReservationStatusUpdateDto(admin.getId(), reservation.getId(), newStatus);

        // when
        reservationService.updateStatusWithAdminAuthorization(adminReservationStatusUpdateDto);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(reservation.getStatus()).isEqualTo(newStatus);
    }

    @DisplayName("관리자는 등록되어 있는 예약을 삭제하면 삭제 상태로 변경되고, 연관된 리뷰도 함께 삭제 상태가 된다.")
    @Test
    void deleteReservationWithAdminAuthorization() {
        // given
        Member admin = entityManager.persist(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        Member mentor = entityManager.persist(new Member(
                "mentorId",
                "MALE",
                "최유리",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
                mentor,
                5000,
                5,
                "잘 지내자, 우리",
                "분명 언젠가 다시 스칠 날 있겠지만 모른척 지나가겠지~",
                "가상의오픈채팅링크"
        ));
        Member mentee = entityManager.persist(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation = entityManager.persist(new Reservation(
                "최선을 다한 넌 받아들이겠지만",
                Status.COMPLETE,
                mentoring,
                mentee
        ));

        Review review = entityManager.persist(new Review(
                4,
                "서툴렀던 난 아직도 기적을 꿈꾼다",
                reservation,
                mentee
        ));

        MentoringStatistics mentoringStatistics = entityManager.persist(MentoringStatistics.defaultOf(mentoring));
        long originalReservationCount = mentoringStatistics.getReservationCount();

        AdminReservationDeleteDto adminReservationDeleteDto
                = new AdminReservationDeleteDto(admin.getId(), reservation.getId());

        // when
        reservationService.deleteReservationWithAdminAuthorization(adminReservationDeleteDto);

        entityManager.flush();
        entityManager.clear();

        // then
        Reservation deletedReservation = (Reservation) entityManager.getEntityManager().createNativeQuery(
                        "SELECT * FROM reservation WHERE id = ?", Reservation.class)
                .setParameter(1, reservation.getId())
                .getSingleResult();

        Review deletedReview = (Review) entityManager.getEntityManager().createNativeQuery(
                        "SELECT * FROM review WHERE id = ?", Review.class)
                .setParameter(1, review.getId())
                .getSingleResult();

        assertSoftly(softly -> {
            softly.assertThat(deletedReview.isDeleted()).isTrue();
            softly.assertThat(deletedReservation.isDeleted()).isTrue();
            softly.assertThat(deletedReservation.getDeletedAt()).isNotNull();
            softly.assertThat(deletedReview.getDeletedAt()).isNotNull();
            softly.assertThat(mentoringStatisticsRepository.findById(mentoring.getId()).get().getReservationCount())
                    .isEqualTo(originalReservationCount - 1);
        });
    }

    @DisplayName("존재하지 않는 예약을 삭제하는 경우 예외가 발생한다.")
    @Test
    void deleteReservationWithAdminAuthorization2() {
        // given
        Member admin = entityManager.persist(new Member(
                "adminId",
                "MALE",
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        Member mentor = entityManager.persist(new Member(
                "mentorId",
                "MALE",
                "최유리",
                new Phone("010-1234-5678"),
                Password.from("password"),
                MemberRole.MENTOR
        ));
        Mentoring mentoring = entityManager.persist(new Mentoring(
                mentor,
                5000,
                5,
                "잘 지내자, 우리",
                "분명 언젠가 다시 스칠 날 있겠지만 모른척 지나가겠지~",
                "가상의오픈채팅링크"
        ));
        Member mentee = entityManager.persist(new Member(
                "menteeId1",
                "MALE",
                "김멘티",
                new Phone("010-1234-5679"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        Reservation reservation = entityManager.persist(new Reservation(
                "최선을 다한 넌 받아들이겠지만",
                Status.COMPLETE,
                mentoring,
                mentee
        ));

        Review review = entityManager.persist(new Review(
                4,
                "서툴렀던 난 아직도 기적을 꿈꾼다",
                reservation,
                mentee
        ));

        Long invalidReservationId = 100L;

        AdminReservationDeleteDto adminReservationDeleteDto
                = new AdminReservationDeleteDto(admin.getId(), invalidReservationId);

        // when // then
        assertThatThrownBy(() -> reservationService.deleteReservationWithAdminAuthorization(adminReservationDeleteDto))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage());
    }
}
