package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.CategoryNotFoundException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.CertificateType;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.infra.S3Uploader;
import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.MentoringRequest;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import fittoring.util.DbCleaner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class MentoringServiceTest {

    @Autowired
    private MentoringService mentoringService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @MockitoBean
    private S3Uploader s3Uploader;

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("멘토링 요약 조회")
    @Nested
    class FindMentoringSummary {

        @DisplayName("필터링 설정이 없는 경우, 모든 멘토링의 요약 정보를 조회할 수 있다.")
        @Test
        void getAllMentoring1() {
            // given
            Member mentor1 = new Member("id1", "MALE", "김트레이너", new Phone("010-4321-9048"), Password.from("pw"));
            Member mentor2 = new Member("id2", "MALE", "박트레이너", new Phone("010-1234-5678"), Password.from("pw"));
            em.persist(mentor1);
            em.persist(mentor2);
            Mentoring mentoring1 = new Mentoring(mentor1, 5000, 3, "컨텐츠1", "자기소개1");
            Mentoring mentoring2 = new Mentoring(mentor2, 5000, 3, "컨텐츠2", "자기소개2");
            em.persist(mentoring1);
            em.persist(mentoring2);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            em.persist(category1);
            em.persist(category2);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring2_2);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = null;
            String categoryTitle2 = null;
            String categoryTitle3 = null;

            MentoringSummaryResponse expected = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                            mentoring1,
                            List.of(categoryMentoring1_1.getCategoryTitle()),
                            image1)
            );

            MentoringSummaryResponse expected2 = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                            mentoring2,
                            List.of(categoryMentoring2_2.getCategoryTitle())
                    )
            );

            // when
            List<MentoringSummaryResponse> actual = mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            );

            // then
            assertThat(actual).containsExactly(expected, expected2);
        }

        @DisplayName("카테고리 필터링 조건을 만족하는 모든 멘토링의 요약 정보를 조회할 수 있다.")
        @Test
        void getAllMentoring2() {
            // given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-3333-9048"), Password.from("pw"));
            Member member2 = new Member("id2", "MALE", "박트레이너", new Phone("010-1234-5678"), Password.from("pw"));
            Member member3 = new Member("id3", "MALE", "이트레이너", new Phone("010-1234-5679"), Password.from("pw"));
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            Mentoring mentoring1 = new Mentoring(member1, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring2 = new Mentoring(member2, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring3 = new Mentoring(member3, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);
            em.persist(mentoring2);
            em.persist(mentoring3);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            Category category3 = new Category("카테고리3");
            em.persist(category1);
            em.persist(category2);
            em.persist(category3);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring2_1 = new CategoryMentoring(category2, mentoring1);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            CategoryMentoring categoryMentoring1_3 = new CategoryMentoring(category1, mentoring3);
            CategoryMentoring categoryMentoring2_3 = new CategoryMentoring(category2, mentoring3);
            CategoryMentoring categoryMentoring3_3 = new CategoryMentoring(category3, mentoring3);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring2_1);
            em.persist(categoryMentoring2_2);
            em.persist(categoryMentoring1_3);
            em.persist(categoryMentoring2_3);
            em.persist(categoryMentoring3_3);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring1.getId());
            Image image2 = new Image("멘토링이미지3url", ImageType.MENTORING_PROFILE, mentoring3.getId());
            em.persist(image1);
            em.persist(image2);

            String categoryTitle1 = category1.getTitle();
            String categoryTitle2 = category2.getTitle();
            String categoryTitle3 = null;

            MentoringSummaryResponse expected = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                            mentoring1,
                            List.of(categoryMentoring1_1.getCategoryTitle(),
                                    categoryMentoring2_1.getCategoryTitle()),
                            image1
                    )
            );

            MentoringSummaryResponse expected2 = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                            mentoring3,
                            List.of(categoryMentoring1_3.getCategoryTitle(),
                                    categoryMentoring2_3.getCategoryTitle(),
                                    categoryMentoring3_3.getCategoryTitle()
                            ),
                            image2
                    )
            );

            // when
            List<MentoringSummaryResponse> actual = mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            );

            // then
            assertThat(actual).containsExactly(expected, expected2);
        }

        @DisplayName("존재하지 않는 카테고리 이름을 필터링하려는 경우 예외가 발생한다.")
        @Test
        void getAllMentoring5() {
            // given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            Member member2 = new Member("id2", "MALE", "박트레이너", new Phone("010-1234-5678"), Password.from("pw"));
            em.persist(member1);
            em.persist(member2);

            Mentoring mentoring1 = new Mentoring(member1, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring2 = new Mentoring(member2, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);
            em.persist(mentoring2);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            em.persist(category1);
            em.persist(category2);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring1_2 = new CategoryMentoring(category1, mentoring2);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring1_2);
            em.persist(categoryMentoring2_2);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = category1.getTitle();
            String categoryTitle2 = "비정상카테고리";
            String categoryTitle3 = null;

            // when
            // then
            assertThatThrownBy(() -> mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            )).isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(BusinessErrorMessage.CATEGORY_NOT_FOUND.getMessage());

        }

        @DisplayName("필터 조건에 해당하는 멘토링이 존재하지 않는 경우, 빈 리스트를 반환한다.")
        @Test
        void getAllMentoring6() {
            // given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            Member member2 = new Member("id2", "MALE", "박트레이너", new Phone("010-1234-5678"), Password.from("pw"));
            em.persist(member1);
            em.persist(member2);

            Mentoring mentoring1 = new Mentoring(member1, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring2 = new Mentoring(member2, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);
            em.persist(mentoring2);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            Category category3 = new Category("카테고리3");
            em.persist(category1);
            em.persist(category2);
            em.persist(category3);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring2_1 = new CategoryMentoring(category2, mentoring1);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring2_1);
            em.persist(categoryMentoring2_2);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = null;
            String categoryTitle2 = null;
            String categoryTitle3 = category3.getTitle();

            // when
            List<MentoringSummaryResponse> actual = mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            );

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("멘토링 정보 조회")
    class FindMentoring {
        @DisplayName("멘토링 id로 멘토링을 조회한다.")
        @Test
        void getMentoring() {
            //given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-3378-9048"), Password.from("pw"));
            em.persist(member1);

            Mentoring mentoring1 = new Mentoring(member1, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);

            Category category1 = new Category("카테고리1");
            em.persist(category1);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            em.persist(categoryMentoring1_1);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, mentoring1.getId());
            em.persist(image1);

            MentoringResponse expected = MentoringResponse.from(
                    mentoring1,
                    List.of(category1.getTitle()),
                    image1
            );

            //when
            MentoringResponse actual = mentoringService.getMentoring(mentoring1.getId());

            //then
            assertThat(actual).isEqualTo(expected);
        }


        @DisplayName("존재하지 않는 멘토링 id로 멘토링을 조회하는 경우 예외가 발생한다.")
        @Test
        void getMentoring2() {
            //given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            em.persist(member1);

            Mentoring mentoring1 = new Mentoring(member1, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
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
                    mentoringService.getMentoring(invalidId))
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
            memberRepository.save(member1);

            MentoringRequest request = new MentoringRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    3,
                    "컨텐츠컨텐츠",
                    List.of()
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");

            categoryRepository.save(category1);
            categoryRepository.save(category2);

            when(s3Uploader.upload(any(), any())).thenReturn(null);

            // when
            MentoringResponse actual = mentoringService.registerMentoring(RegisterMentoringDto.of(member1.getId(), request, null, null));

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.price()).isEqualTo(request.price());
                softAssertions.assertThat(actual.categories()).containsExactlyInAnyOrder("근육증가", "다이어트");
                softAssertions.assertThat(actual.introduction()).isEqualTo(request.introduction());
                softAssertions.assertThat(actual.career()).isEqualTo(request.career());
                softAssertions.assertThat(actual.content()).isEqualTo(request.content());
                softAssertions.assertThat(actual.profileImageUrl()).isNull();
            });
        }

        @DisplayName("프로필 이미지를 포함하여 멘토링을 등록할 수 있다.")
        @Test
        void registerMentoringProfile() throws IOException {
            //given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            memberRepository.save(member1);

            MentoringRequest request = new MentoringRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    3,
                    "컨텐츠컨텐츠",
                    List.of()
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");

            categoryRepository.save(category1);
            categoryRepository.save(category2);

            MockMultipartFile imageFile = new MockMultipartFile("testProfile",
                    "testProfile".getBytes(StandardCharsets.UTF_8));
            String profileImageS3Url = "profileImageS3Url";
            when(s3Uploader.upload(imageFile, "profile-image")).thenReturn(profileImageS3Url);

            // when
            MentoringResponse actual = mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            member1.getId(),
                            request,
                            imageFile,
                            null
                    )
            );

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.price()).isEqualTo(request.price());
                softAssertions.assertThat(actual.categories()).containsExactlyInAnyOrder("근육증가", "다이어트");
                softAssertions.assertThat(actual.introduction()).isEqualTo(request.introduction());
                softAssertions.assertThat(actual.career()).isEqualTo(request.career());
                softAssertions.assertThat(actual.content()).isEqualTo(request.content());
                softAssertions.assertThat(actual.profileImageUrl()).isEqualTo(profileImageS3Url);
            });
        }

        @DisplayName("프로필 이미지와 자격증을 포함하여 멘토링을 등록할 수 있다.")
        @Test
        void registerMentoringProfileCertificates() throws IOException {
            //given
            Member member1 = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
            memberRepository.save(member1);

            CertificateInfo certificateInfo1 = new CertificateInfo(CertificateType.LICENSE, "제1종 보통 운전면허");
            CertificateInfo certificateInfo2 = new CertificateInfo(CertificateType.AWARD, "광진구 건강 청년 선발 대회 준우승");

            MentoringRequest request = new MentoringRequest(
                    5000,
                    List.of("근육증가", "다이어트"),
                    "자기소개",
                    3,
                    "컨텐츠컨텐츠",
                    List.of(certificateInfo1, certificateInfo2)
            );

            Category category1 = new Category("근육증가");
            Category category2 = new Category("다이어트");

            categoryRepository.save(category1);
            categoryRepository.save(category2);

            MockMultipartFile profileImageFile = new MockMultipartFile("testProfile",
                    "testProfile".getBytes(StandardCharsets.UTF_8));
            String profileImageS3Url = "profileImageS3Url";
            when(s3Uploader.upload(profileImageFile, "profile-image")).thenReturn(profileImageS3Url);

            MockMultipartFile certificateImageFile1 = new MockMultipartFile("testCertificate1",
                    "testCertificate1".getBytes(StandardCharsets.UTF_8));
            MockMultipartFile certificateImageFile2 = new MockMultipartFile("testCertificate2",
                    "testCertificate2".getBytes(StandardCharsets.UTF_8));

            String certificateImageS3Url1 = "testCertificate1ImageS3Url";
            String certificateImageS3Url2 = "testCertificate2ImageS3Url";
            when(s3Uploader.upload(certificateImageFile1, "certificate-image")).thenReturn(certificateImageS3Url1);
            when(s3Uploader.upload(certificateImageFile2, "certificate-image")).thenReturn(certificateImageS3Url2);

            // when
            MentoringResponse actual = mentoringService.registerMentoring(
                    RegisterMentoringDto.of(
                            member1.getId(),
                            request,
                            profileImageFile,
                            List.of(certificateImageFile1, certificateImageFile2)
                    )
            );

            // then
            Image certificateImage1 = imageRepository.findByImageTypeAndRelationId(ImageType.CERTIFICATE, 1L).get();
            Image certificateImage2 = imageRepository.findByImageTypeAndRelationId(ImageType.CERTIFICATE, 2L).get();

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.price()).isEqualTo(request.price());
                softAssertions.assertThat(actual.categories()).containsExactlyInAnyOrder("근육증가", "다이어트");
                softAssertions.assertThat(actual.introduction()).isEqualTo(request.introduction());
                softAssertions.assertThat(actual.career()).isEqualTo(request.career());
                softAssertions.assertThat(actual.content()).isEqualTo(request.content());
                softAssertions.assertThat(actual.profileImageUrl()).isEqualTo(profileImageS3Url);
                softAssertions.assertThat(certificateImage1.getUrl()).isEqualTo(certificateImageS3Url1);
                softAssertions.assertThat(certificateImage2.getUrl()).isEqualTo(certificateImageS3Url2);
            });
        }
    }
}
