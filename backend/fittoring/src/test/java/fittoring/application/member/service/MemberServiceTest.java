package fittoring.application.member.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.presentation.dto.request.MemberInfoUpdateRequest;
import fittoring.application.member.presentation.dto.response.MyInfoResponse;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends IntegrationTestSupport {

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
        assertSoftly(softAssertions -> {
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
        assertSoftly(softAssertions -> {
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
        assertSoftly(softAssertions -> {
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
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("회원(멘토, 멘티)은 자신의 회원 정보인 이름, 성별, 비밀번호, 전화번호를 수정할 수 있다.")
    @Test
    void updateMemberInfo() {
        //given
        String rawName = "이름";
        String rawGender = "MALE";
        String rawPhoneNumber = "010-1234-5678";
        Password rawPassword = Password.from("password");
        Member member = memberRepository.save(
                new Member(
                        "menteeId",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        rawPassword
                )
        );

        String newName = "newName";
        String newGender = "newGender";
        String newPassword = "newPassword";
        String newPhoneNumber = "010-5678-9123";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                newGender,
                newPassword,
                newPhoneNumber
        );

        //when
        memberService.updateMemberInfo(member.getId(), request);

        //then
        Member actual = memberRepository.findById(member.getId())
                .orElse(null);

        assertSoftly(softly -> {
            softly.assertThat(actual).isNotNull();
            softly.assertThat(actual.getName()).isNotEqualTo(rawName);
            softly.assertThat(actual.getGender()).isNotEqualTo(rawGender);
            softly.assertThat(actual.getPassword()).isNotEqualTo(rawPassword.getPassword());
            softly.assertThat(actual.getPhoneNumber()).isNotEqualTo(rawPhoneNumber);
        });
    }

    @DisplayName("회원(멘토, 멘티)은 자신의 이름, 성별, 비밀번호, 전화번호 중 일부를 선택적으로 수정할 수 있다.")
    @Test
    void updateMemberInfo2() {
        //given
        String rawName = "이름";
        String rawGender = "MALE";
        String rawPhoneNumber = "010-1234-5678";
        Password rawPassword = Password.from("password");
        Member member = memberRepository.save(
                new Member(
                        "menteeId",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        rawPassword
                )
        );

        String newName = "newName";
        String newPhoneNumber = "010-5678-9123";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                null,
                null,
                newPhoneNumber
        );

        //when
        memberService.updateMemberInfo(member.getId(), request);

        //then
        Member actual = memberRepository.findById(member.getId())
                .orElse(null);

        assertSoftly(softly -> {
            softly.assertThat(actual).isNotNull();
            softly.assertThat(actual.getName()).isNotEqualTo(rawName);
            softly.assertThat(actual.getGender()).isEqualTo(rawGender);
            softly.assertThat(actual.getPassword()).isEqualTo(rawPassword.getPassword());
            softly.assertThat(actual.getPhoneNumber()).isNotEqualTo(rawPhoneNumber);
        });
    }
}
