package fittoring.application.member.service;

import fittoring.application.FixtureUtil;
import fittoring.application.SpringBootTestSupport;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.presentation.dto.response.MyInfoResponse;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends SpringBootTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MemberService memberService;

    @DisplayName("멘티는 로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfoForMentee() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());

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

        memberRepository.save(member);
        mentoringRepository.save(mentoring);

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

        memberRepository.save(member);
        mentoringRepository.save(mentoring);

        Image image = new Image(
                "profileImageUrl",
                ImageType.MENTORING_PROFILE,
                mentoring.getId(),
                "baseName"
        );
        imageRepository.save(image);

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
        Member member = memberRepository.save(FixtureUtil.getTestMentee());

        // when
        MyInfoSummaryResponse memberInfo = memberService.getMemberInfoSummary(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }
}
