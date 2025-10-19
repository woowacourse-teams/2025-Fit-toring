package fittoring.application.member.service;

import fittoring.application.FixtureUtil;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.presentation.dto.response.MyInfoResponse;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.infrastructure.image.KeyBuilder;
import fittoring.util.DbCleaner;
import org.assertj.core.api.SoftAssertions;
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
        KeyBuilder.class,
        MemberService.class,
        ImageService.class,
        QueryDslConfig.class,
        JpaConfiguration.class,
        MentoringPaginationHelper.class
})
@DataJpaTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("멘티는 로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfoForMentee() {
        // given
        Member member = em.persist(FixtureUtil.getTestMentee());

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isNull();
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(member.getLoginId());
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(member.getGender());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("멘토링 이미지가 없는 멘토는 로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfoForMentorWithoutImage() {
        // given
        Member member = FixtureUtil.getTestMentor();
        Mentoring mentoring = FixtureUtil.getTestMentoring(member);

        em.persist(member);
        em.persist(mentoring);

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isNull();
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(member.getLoginId());
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(member.getGender());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("멘토링 이미지가 있는 멘토는 로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfoForMentorWithImage() {
        // given
        Member member = FixtureUtil.getTestMentee();
        Mentoring mentoring = FixtureUtil.getTestMentoring(member);

        em.persist(member);
        em.persist(mentoring);

        Image image = new Image(
                "profileImageUrl",
                ImageType.MENTORING_PROFILE,
                mentoring.getId(),
                null
        );
        em.persist(image);

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isEqualTo(image.getUrl());
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(member.getLoginId());
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(member.getGender());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("멘티는 로그인 상태에서 내 요약 정보를 조회할 수 있다..")
    @Test
    void getMyInfoSummary() {
        // given
        Member member = em.persist(FixtureUtil.getTestMentee());

        // when
        MyInfoSummaryResponse memberInfo = memberService.getMemberInfoSummary(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }
}
