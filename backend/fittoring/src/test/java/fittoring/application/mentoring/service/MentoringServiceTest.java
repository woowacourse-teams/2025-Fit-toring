package fittoring.application.mentoring.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.image.repository.ImageRepository;
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
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.password.Password;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MentoringServiceTest extends IntegrationTestSupport {

    @Autowired
    private MentoringService mentoringService;

    @Autowired
    private CategoryRepository categoryRepository;

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

    @DisplayName("관리자가 멘토링을 삭제하면 연관된 객체도 함께 삭제 상태가 된다.")
    @Test
    void deleteByAdmin() {
        // given
        Member mentor = FixtureUtil.getTestMentor();
        Member admin = FixtureUtil.getTestAdmin();
        memberRepository.saveAll(List.of(mentor, admin));

        LoginInfo adminLoginId = new LoginInfo(admin.getId());

        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
        Long mentoringId = mentoring.getId();

        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Category category1 = new Category("카테고리1");
        Category category2 = new Category("카테고리2");
        categoryRepository.saveAll(List.of(category1, category2));

        CategoryMentoring categoryMentoring = new CategoryMentoring(category1, mentoring);
        categoryMentoringRepository.save(categoryMentoring);
        categoryMentoringRepository.save(new CategoryMentoring(category2, mentoring));

        imageRepository.save(new Image(
                "멘토링이미지1url",
                ImageType.MENTORING_PROFILE,
                mentoringId,
                null
        ));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());
        Reservation reservation1 = reservationRepository.save(
                FixtureUtil.getTestCompletedReservation(mentoring, mentee));
        reservationRepository.save(FixtureUtil.getTestPendingReservation(mentoring, mentee));
        Review review = reviewRepository.save(FixtureUtil.getTestReview(reservation1, mentee));

        Certificate certificate = certificateRepository.save(
                new Certificate(CertificateType.LICENSE, "자격증1", mentoring));

        // when
        mentoringService.deleteMentoringByAdmin(adminLoginId, mentoringId);

        // then
        Review deletedReview = reviewRepository.findDeletedById(review.getId());
        Reservation deletedReservation = reservationRepository.findDeletedById(reservation1.getId());
        Certificate deletedCertificate = certificateRepository.findDeletedById(certificate.getId());
        CategoryMentoring deletedCategoryMentoring = categoryMentoringRepository.findDeletedById(
                categoryMentoring.getId());
        MentoringStatistics deletedMentoringStatistics = mentoringStatisticsRepository.findDeletedByMentoringId(
                mentoring.getId());
        Mentoring deletedMentoring = mentoringRepository.findDeletedById(mentoring.getId());

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

    @Nested
    @DisplayName("멘토링 정보 조회")
    class FindMentoring {

        @DisplayName("멘토링 id로 멘토링을 조회한다.")
        @Test
        void getMentoring() {
            // given
            List<Member> savedMembers = memberRepository.saveAll(
                    List.of(FixtureUtil.getTestMentor(), FixtureUtil.getTestMentee()));
            Member mentor = savedMembers.get(0);
            Member mentee = savedMembers.get(1);

            Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));
            mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

            Category category = categoryRepository.save(new Category("카테고리1"));
            categoryMentoringRepository.save(new CategoryMentoring(category, mentoring));

            Image profile = imageRepository.save(
                    new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring.getId(), "baseName")
            );

            Reservation reservation1 = reservationRepository.save(
                    FixtureUtil.getTestCompletedReservation(mentoring, mentee)
            );
            Reservation reservation2 = reservationRepository.save(
                    FixtureUtil.getTestCompletedReservation(mentoring, mentee)
            );
            mentoringStatisticsRepository.updateReservationCountPlus(mentoring.getId());
            mentoringStatisticsRepository.updateReservationCountPlus(mentoring.getId());

            reviewRepository.save(FixtureUtil.getTestReview(reservation1, mentee));
            reviewRepository.save(FixtureUtil.getTestReview(reservation2, mentee));
            mentoringStatisticsRepository.updateReviewStatisticsPlus(mentoring.getId(), 4);
            mentoringStatisticsRepository.updateReviewStatisticsPlus(mentoring.getId(), 5);

            MentoringResponse expected = MentoringResponse.of(
                    mentoring,
                    List.of(category.getTitle()),
                    profile,
                    List.of(),
                    4.5,
                    2
            );

            // when
            MentoringResponse actual = mentoringService.getMentoringWithRelationsById(mentoring.getId());

            // then
            assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 멘토링 id로 멘토링을 조회하는 경우 예외가 발생한다.")
        @Test
        void getMentoring2() {
            //given
            Member member = FixtureUtil.getTestMentee();
            memberRepository.save(member);

            Mentoring mentoring = FixtureUtil.getTestMentoring(member);
            mentoringRepository.save(mentoring);

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
        void registerMentoring() {
            //given
            Member member = memberRepository.save(FixtureUtil.getTestMentee());

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
            categoryRepository.saveAll(List.of(category1, category2));

            // when
            // then
            assertThatCode(() ->
                    mentoringService.registerMentoring(
                            RegisterMentoringDto.of(
                                    member.getId(),
                                    request
                            )))
                    .doesNotThrowAnyException();
        }

        @DisplayName("프로필 이미지를 포함하여 멘토링을 등록할 수 있다.")
        @Test
        void registerMentoringProfile() {
            // given
            Member member = memberRepository.save(FixtureUtil.getTestMentee());
            String profileImageUrl = "가상의 이미지 주소";

            MentoringRegisterRequest request = new MentoringRegisterRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    profileImageUrl,
                    3,
                    "컨텐츠컨텐츠",
                    "가상의카카오오픈채팅",
                    List.of()
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");
            categoryRepository.saveAll(List.of(category1, category2));

            // when & then
            assertThatCode(() -> mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            member.getId(),
                            request
                    ))).doesNotThrowAnyException();
        }

        @DisplayName("프로필 이미지와 자격증을 포함하여 멘토링을 등록할 수 있다.")
        @Test
        void registerMentoringProfileCertificates() throws IOException {
            // given
            Member member = memberRepository.save(FixtureUtil.getTestMentee());

            CertificateInfoRequest certificateInfo1 = new CertificateInfoRequest(CertificateType.LICENSE,
                    "제1종 보통 운전면허",
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
            categoryRepository.saveAll(List.of(category1, category2));

            // when & then
            assertThatCode(() -> mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            member.getId(),
                            request
                    ))).doesNotThrowAnyException();
        }

        @DisplayName("멘토링이 저장될 때 멘토링 통계 정보도 저장된다.")
        @Test
        void saveMentoringStatistics() {
            //given
            Member member = memberRepository.save(FixtureUtil.getTestMentee());

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
            categoryRepository.saveAll(List.of(category1, category2));

            //when
            mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            member.getId(),
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
            Member mentor = memberRepository.save(FixtureUtil.getTestMentee());
            Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

            categoryRepository.save(new Category("다이어트"));

            ModifyMentoringDto modifyMentoringDto = new ModifyMentoringDto(
                    mentoring.getId(),
                    mentor.getId(),
                    1000,
                    List.of("다이어트"),
                    "수정된 긴 글 소개",
                    5,
                    "수정된 한 줄 소개",
                    "가상의오픈채팅링크",
                    "수정된 이미지 주소",
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
                softAssertions.assertThat(changedMentoring.getPrice()).isEqualTo(modifyMentoringDto.price());
                softAssertions.assertThat(changedMentoring.getIntroduction())
                        .isEqualTo(modifyMentoringDto.introduction());
                softAssertions.assertThat(changedMentoring.getCareer()).isEqualTo(modifyMentoringDto.career());
                softAssertions.assertThat(changedMentoring.getContent()).isEqualTo(modifyMentoringDto.content());
                softAssertions.assertThat(changedCategories).containsExactlyInAnyOrder("다이어트");
                softAssertions.assertThat(changedProfileImage.getUrl()).isEqualTo(modifyMentoringDto.profileImageUrl());
                softAssertions.assertThat(certificateImage.getUrl()).isEqualTo("자격증명 이미지 1");
            });
        }

        @DisplayName("존재하지 않는 멘토링을 수정하려고 하면 예외가 발생한다")
        @Test
        void modifyMentoringFail1() {
            // given
            Member mentor = memberRepository.save(FixtureUtil.getTestMentee());

            categoryRepository.save(new Category("다이어트"));

            ModifyMentoringDto modifyMentoringDto = new ModifyMentoringDto(
                    900L,
                    mentor.getId(),
                    1000,
                    List.of("다이어트"),
                    "수정된 긴 글 소개",
                    5,
                    "수정된 한 줄 소개",
                    "가상의오픈채팅링크",
                    "수정된 이미지 주소",
                    List.of(new CertificateInfoRequest(CertificateType.AWARD, "최우수상", "자격증명 이미지 1"))
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
            Member mentor = memberRepository.save(FixtureUtil.getTestMentee());
            Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

            Member invalidMember = memberRepository.save(new Member(
                    "id2",
                    "MALE",
                    "박트레이너",
                    new Phone("010-1234-9021"),
                    Password.from("pw")
            ));

            categoryRepository.save(new Category("다이어트"));

            ModifyMentoringDto modifyMentoringDto = new ModifyMentoringDto(
                    mentoring.getId(),
                    invalidMember.getId(),
                    1000,
                    List.of("다이어트"),
                    "수정된 긴 글 소개",
                    5,
                    "수정된 한 줄 소개",
                    "가상의오픈채팅링크",
                    "수정된 이미지 주소",
                    List.of(new CertificateInfoRequest(CertificateType.AWARD, "최우수상", "자격증명 이미지 1"))
            );

            // when
            // then
            assertThatThrownBy(() -> mentoringService.modifyMentoring(modifyMentoringDto))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage(BusinessErrorMessage.MENTOR_NOT_SAME.getMessage());
        }
    }
}
