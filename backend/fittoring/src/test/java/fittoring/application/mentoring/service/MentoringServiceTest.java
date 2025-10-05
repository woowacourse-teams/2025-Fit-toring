package fittoring.application.mentoring.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.presentation.dto.request.CertificateInfoRequest;
import fittoring.application.mentoring.presentation.dto.request.MentoringRegisterRequest;
import fittoring.application.mentoring.presentation.dto.response.MentoringResponse;
import fittoring.application.mentoring.repository.CategoryMentoringRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.mentoring.service.dto.ModifyMentoringDto;
import fittoring.application.mentoring.service.dto.RegisterMentoringDto;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import fittoring.util.DbCleaner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
class MentoringServiceTest {

    @MockitoBean
    private PresignedUrlService presignedUrlService;

    @Autowired
    private MentoringService mentoringService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @Transactional
    @DisplayName("관리자가 멘토링을 삭제하면 연관된 객체도 함께 삭제 상태가 된다.")
    @Test
    void deleteByAdmin() {
        // given
        Member mentor = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
        Member admin = new Member("admin", "MALE", "관리자", new Phone("010-0000-0000"), Password.from("pw"),
                MemberRole.ADMIN);
        memberRepository.save(mentor);
        memberRepository.save(admin);
        LoginInfo adminLoginId = new LoginInfo(admin.getId());

        Mentoring mentoring = new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개", "가상의카카오오픈채팅");
        mentoringRepository.save(mentoring);
        Long mentoringId = mentoring.getId();

        MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(mentoring);
        mentoringStatisticsRepository.save(mentoringStatistics);

        Category category1 = new Category("카테고리1");
        Category category2 = new Category("카테고리2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring);
        CategoryMentoring categoryMentoring2_1 = new CategoryMentoring(category2, mentoring);
        categoryMentoringRepository.save(categoryMentoring1_1);
        categoryMentoringRepository.save(categoryMentoring2_1);

        Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoringId);
        imageRepository.save(image1);

        Reservation reservation = new Reservation("예약내용", Status.PENDING, mentoring, mentor);
        reservationRepository.save(reservation);

        Reservation reservation2 = new Reservation("예약내용", Status.PENDING, mentoring, mentor);
        reservationRepository.save(reservation2);

        Member mentee = new Member("멘티id", "MALE", "김멘티", new Phone("010-1234-1234"), Password.from("password"));
        memberRepository.save(mentee);
        Review review = new Review(1, "리뷰내용", reservation, mentee);
        reviewRepository.save(review);

        Certificate certificate = new Certificate(CertificateType.LICENSE, "자격증1", mentoring);
        certificateRepository.save(certificate);

        // when
        mentoringService.deleteMentoringByAdmin(adminLoginId, mentoringId);
        em.flush();
        em.clear();

        // then
        Review deletedReview = (Review) em.createNativeQuery(
                        "SELECT * FROM review WHERE id = ?", Review.class)
                .setParameter(1, review.getId())
                .getSingleResult();

        Reservation deletedReservation = (Reservation) em.createNativeQuery(
                        "SELECT * FROM reservation WHERE id = ?", Reservation.class)
                .setParameter(1, reservation.getId())
                .getSingleResult();

        Certificate deletedCertificate = (Certificate) em.createNativeQuery(
                        "SELECT * FROM certificate WHERE id = ?", Certificate.class)
                .setParameter(1, certificate.getId())
                .getSingleResult();

        CategoryMentoring deletedCategoryMentoring = (CategoryMentoring) em.createNativeQuery(
                        "SELECT * FROM category_mentoring WHERE id = ?", CategoryMentoring.class)
                .setParameter(1, categoryMentoring1_1.getId())
                .getSingleResult();

        MentoringStatistics deletedMentoringStatistics = (MentoringStatistics) em.createNativeQuery(
                        "SELECT * FROM mentoring_statistics WHERE mentoring_id = ?", MentoringStatistics.class)
                .setParameter(1, mentoring.getId())
                .getSingleResult();

        Mentoring deletedMentoring = (Mentoring) em.createNativeQuery(
                        "SELECT * FROM mentoring WHERE id = ?", Mentoring.class)
                .setParameter(1, mentoring.getId())
                .getSingleResult();
        em.flush();
        em.clear();

        SoftAssertions.assertSoftly(softly -> {
                    assertThatThrownBy(() -> mentoringService.getMentoringWithRelationsById(mentoringId))
                            .isInstanceOf(MentoringNotFoundException.class);
                    assertThat(categoryMentoringRepository.findTitlesByMentoringId(
                            mentoringId)).isEmpty();
                    assertThat(categoryRepository.existsByTitle("카테고리1")).isEqualTo(true);
                    assertThat(categoryRepository.existsByTitle("카테고리2")).isEqualTo(true);
                    assertThat(reservationRepository.findAll()).isEmpty();
                    assertThat(reviewRepository.findAll()).isEmpty();
                    assertThat(certificateRepository.existsById(certificate.getId())).isEqualTo(false);
                    assertThat(deletedReview.isDeleted()).isTrue();
                    assertThat(deletedReservation.isDeleted()).isTrue();
                    assertThat(deletedCertificate.isDeleted()).isTrue();
                    assertThat(deletedCategoryMentoring.isDeleted()).isTrue();
                    assertThat(deletedMentoringStatistics.isDeleted()).isTrue();
                    assertThat(deletedMentoring.isDeleted()).isTrue();
                }
        );
    }

    @Transactional
    @Nested
    @DisplayName("멘토링 정보 조회")
    class FindMentoring {

        @DisplayName("멘토링 id로 멘토링을 조회한다.")
        @Test
        void getMentoring() {
            //given
            Member mentor = new Member("id1", "MALE", "김트레이너", new Phone("010-3378-9048"), Password.from("pw"));
            em.persist(mentor);

            Member mentee = new Member("id2", "MALE", "이멘티", new Phone("010-1234-5678"), Password.from("pw"));
            em.persist(mentee);

            Mentoring mentoring1 = new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개", "가상의카카오오픈채팅");
            em.persist(mentoring1);
            mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring1));

            Category category1 = new Category("카테고리1");
            em.persist(category1);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            em.persist(categoryMentoring1_1);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring1.getId());
            em.persist(image1);

            Reservation reservation1 = new Reservation("예약 코멘트1", Status.COMPLETE, mentoring1, mentee);
            em.persist(reservation1);
            Reservation reservation2 = new Reservation("예약 코멘트2", Status.COMPLETE, mentoring1, mentee);
            em.persist(reservation2);
            mentoringStatisticsRepository.updateReservationCountPlus(mentoring1.getId());
            mentoringStatisticsRepository.updateReservationCountPlus(mentoring1.getId());

            Review review1 = new Review(4, "리뷰 코멘트", reservation1, mentee);
            em.persist(review1);
            Review review2 = new Review(5, "리뷰 코멘트", reservation2, mentee);
            em.persist(review2);
            mentoringStatisticsRepository.updateReviewStatisticsPlus(mentoring1.getId(), 4);
            mentoringStatisticsRepository.updateReviewStatisticsPlus(mentoring1.getId(), 5);

            MentoringResponse expected = MentoringResponse.of(
                    mentoring1,
                    List.of(category1.getTitle()),
                    image1,
                    List.of(),
                    4.5,
                    2
            );

            //when
            MentoringResponse actual = mentoringService.getMentoringWithRelationsById(mentoring1.getId());

            //then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 멘토링 id로 멘토링을 조회하는 경우 예외가 발생한다.")
        @Test
        void getMentoring2() {
            //given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            em.persist(member1);

            Mentoring mentoring1 = new Mentoring(member1, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개", "가상의카카오오픈채팅");
            em.persist(mentoring1);

            Category category1 = new Category("카테고리1");
            em.persist(category1);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            em.persist(categoryMentoring1_1);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring1.getId());
            em.persist(image1);

            Long invalidId = 100L;

            //when
            //then
            assertThatThrownBy(() ->
                    mentoringService.getMentoringWithRelationsById(invalidId))
                    .isInstanceOf(MentoringNotFoundException.class)
                    .hasMessageStartingWith(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("멘토링 등록")
    class RegisterMentoring {

        @DisplayName("아무 이미지 없이 멘토링을 등록할 수 있다.")
        @Test
        void registerMentoring() throws IOException {
            //given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            Member savedMentor = memberRepository.save(member1);

            MentoringRegisterRequest request = new MentoringRegisterRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    null,
                    3,
                    "컨텐츠컨텐츠",
                    "가상의카카오오픈채팅",
                    List.of()
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");

            categoryRepository.save(category1);
            categoryRepository.save(category2);

            // when
            // then
            assertThatCode(() ->
                    mentoringService.registerMentoring(
                            RegisterMentoringDto.of(
                                    savedMentor.getId(),
                                    request
                            )))
                    .doesNotThrowAnyException();
        }

        @DisplayName("프로필 이미지를 포함하여 멘토링을 등록할 수 있다.")
        @Test
        void registerMentoringProfile() {
            // given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            memberRepository.save(member1);

            MentoringRegisterRequest request = new MentoringRegisterRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    "가상의 이미지 주소",
                    3,
                    "컨텐츠컨텐츠",
                    "가상의카카오오픈채팅",
                    List.of()
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            MockMultipartFile imageFile = new MockMultipartFile("testProfile",
                    "testProfile".getBytes(StandardCharsets.UTF_8));

            // when & then
            assertThatCode(() -> mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            member1.getId(),
                            request
                    ))).doesNotThrowAnyException();
        }

        @DisplayName("프로필 이미지와 자격증을 포함하여 멘토링을 등록할 수 있다.")
        @Test
        void registerMentoringProfileCertificates() throws IOException {
            // given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            memberRepository.save(member1);

            CertificateInfoRequest certificateInfo1 = new CertificateInfoRequest(CertificateType.LICENSE, "제1종 보통 운전면허",
                    "이미지 주소1");
            CertificateInfoRequest certificateInfo2 = new CertificateInfoRequest(CertificateType.AWARD,
                    "광진구 건강 청년 선발 대회 준우승",
                    "이미지 주소2");

            MentoringRegisterRequest request = new MentoringRegisterRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    null,
                    3,
                    "컨텐츠컨텐츠",
                    "가상의카카오오픈채팅",
                    List.of(certificateInfo1, certificateInfo2)
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            MockMultipartFile profileImageFile = new MockMultipartFile("testProfile",
                    "testProfile".getBytes(StandardCharsets.UTF_8));
            MockMultipartFile certificateImageFile1 = new MockMultipartFile("testCertificate1",
                    "testCertificate1".getBytes(StandardCharsets.UTF_8));
            MockMultipartFile certificateImageFile2 = new MockMultipartFile("testCertificate2",
                    "testCertificate2".getBytes(StandardCharsets.UTF_8));

            // when & then
            assertThatCode(() -> mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            member1.getId(),
                            request
                    ))).doesNotThrowAnyException();
        }

        @DisplayName("멘토링이 저장될 때 멘토링 통계 정보도 저장된다.")
        @Test
        void saveMentoringStatistics() {
            //given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            Member savedMentor = memberRepository.save(member1);

            MentoringRegisterRequest request = new MentoringRegisterRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    null,
                    3,
                    "컨텐츠컨텐츠",
                    "가상의카카오오픈채팅",
                    List.of()
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");

            categoryRepository.save(category1);
            categoryRepository.save(category2);

            //when
            mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            savedMentor.getId(),
                            request
                    )
            );

            //then
            Optional<MentoringStatistics> byId = mentoringStatisticsRepository.findById(1L);
            assertThat(byId.get()).isNotNull();
        }
    }

    @Nested
    @DisplayName("멘토링 수정")
    class ModifyMentoring {

        @DisplayName("개설된 멘토링을 수정할 수 있다.")
        @Test
        void modifyMentoring() {
            // given
            Member mentor = memberRepository.save(new Member(
                    "id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw")
            ));
            Category category1 = categoryRepository.save(new Category("근육증가"));
            categoryRepository.save(new Category("다이어트"));

            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    mentor, 5000, 3, "한 줄 소개", "긴 글 소개", "가상의오픈채팅링크"
            ));
            categoryMentoringRepository.save(new CategoryMentoring(category1, mentoring));
            Certificate certificate = certificateRepository.save(new Certificate(
                    CertificateType.LICENSE, "운전면허증", mentoring
            ));

            int newPrice = 1000;
            String newCategory = "다이어트";
            String newIntroduction = "수정된 긴 글 소개";
            String newImageUrl = "수정된 이미지 주소";
            int newCareer = 5;
            String newContent = "수정된 한 줄 소개";

            ModifyMentoringDto modifyMentoringDto = new ModifyMentoringDto(
                    mentoring.getId(),
                    mentor.getId(),
                    newPrice,
                    List.of(newCategory),
                    newIntroduction,
                    newCareer,
                    newContent,
                    "가상의오픈채팅링크",
                    newImageUrl,
                    List.of(new CertificateInfoRequest(CertificateType.AWARD, "최우수상", "자격증명 이미지 1"))
            );

            given(presignedUrlService.isObjectExistsFromUrl(anyString()))
                    .willReturn(true);
            given(presignedUrlService.isObjectExistsFromKey(anyString()))
                    .willReturn(true);

            // when
            mentoringService.modifyMentoring(modifyMentoringDto);

            // then
            Mentoring changedMentoring = mentoringRepository.findById(mentoring.getId()).get();
            List<String> changedCategories = categoryMentoringRepository.findTitlesByMentoringId(mentoring.getId());
            Image changedProfileImage = imageRepository.findByImageTypeAndRelationIdAndImageVariant(
                    ImageType.MENTORING_PROFILE,
                    mentoring.getId(),
                    ImageVariant.DEFAULT
            ).get();

            Certificate changedCertificate = certificateRepository.findAllByMentoringId(mentoring.getId()).getLast();
            Image certificateImage = imageRepository.findByImageTypeAndRelationIdAndImageVariant(
                    ImageType.CERTIFICATE,
                    changedCertificate.getId(),
                    ImageVariant.DEFAULT
            ).get();

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(changedMentoring.getPrice()).isEqualTo(newPrice);
                softAssertions.assertThat(changedMentoring.getIntroduction()).isEqualTo(newIntroduction);
                softAssertions.assertThat(changedMentoring.getCareer()).isEqualTo(newCareer);
                softAssertions.assertThat(changedMentoring.getContent()).isEqualTo(newContent);
                softAssertions.assertThat(changedCategories).containsExactlyInAnyOrder("다이어트");
                softAssertions.assertThat(changedProfileImage.getUrl()).isEqualTo(newImageUrl);
                softAssertions.assertThat(certificateImage.getUrl()).isEqualTo("자격증명 이미지 1");
            });
        }

        @DisplayName("존재하지 않는 멘토링을 수정하려고 하면 예외가 발생한다")
        @Test
        void modifyMentoringFail1() {
            // given
            Member mentor = memberRepository.save(new Member(
                    "id1",
                    "MALE",
                    "김트레이너",
                    new Phone("010-1234-9048"),
                    Password.from("pw")
            ));

            int newPrice = 1000;
            String newCategory = "다이어트";
            String newIntroduction = "수정된 긴 글 소개";
            String newImageUrl = "수정된 이미지 URL";
            int newCareer = 5;
            String newContent = "수정된 한 줄 소개";

            ModifyMentoringDto modifyMentoringDto = new ModifyMentoringDto(
                    999L,
                    mentor.getId(),
                    newPrice,
                    List.of(newCategory),
                    newIntroduction,
                    newCareer,
                    newContent,
                    "가상의오픈채팅링크",
                    newImageUrl,
                    null
            );

            // when
            // then
            assertThatThrownBy(() -> mentoringService.modifyMentoring(modifyMentoringDto))
                    .isInstanceOf(MentoringNotFoundException.class)
                    .hasMessage(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
        }

        @DisplayName("본인이 개설하지 않은 멘토링을 수정하려고 하면 예외가 발생한다")
        @Test
        void modifyMentoringFail2() {
            // given
            Member mentor = memberRepository.save(new Member(
                    "id1",
                    "MALE",
                    "김트레이너",
                    new Phone("010-1234-9048"),
                    Password.from("pw")
            ));
            Mentoring mentoring = mentoringRepository.save(new Mentoring(
                    mentor,
                    5000,
                    3,
                    "한 줄 소개",
                    "긴 글 소개",
                    "가상의오픈채팅링크"
            ));

            Member invalidMember = memberRepository.save(new Member(
                    "id2",
                    "MALE",
                    "박트레이너",
                    new Phone("010-1234-9021"),
                    Password.from("pw")
            ));
            int newPrice = 1000;
            String newCategory = "다이어트";
            String newIntroduction = "수정된 긴 글 소개";
            String newImageUrl = "수정된 이미지 URL";
            int newCareer = 5;
            String newContent = "수정된 한 줄 소개";

            ModifyMentoringDto modifyMentoringDto = new ModifyMentoringDto(
                    mentoring.getId(),
                    invalidMember.getId(),
                    newPrice,
                    List.of(newCategory),
                    newIntroduction,
                    newCareer,
                    newContent,
                    "가상의오픈채팅링크",
                    newImageUrl,
                    null
            );

            // when
            // then
            assertThatThrownBy(() -> mentoringService.modifyMentoring(modifyMentoringDto))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage(BusinessErrorMessage.MENTOR_NOT_SAME.getMessage());
        }
    }
}
