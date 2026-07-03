package fittoring.application.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicatePhoneException;
import fittoring.application.exception.EmptyRequestException;
import fittoring.application.exception.InvalidImageKeyException;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.presentation.dto.request.MemberInfoUpdateRequest;
import fittoring.application.member.presentation.dto.response.MyInfoResponse;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import org.assertj.core.api.SoftAssertions;
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
        Member member = memberRepository.save(FixtureUtil.testMentee());

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isNull();
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(member.getLoginId());
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(member.getGender());
            softAssertions.assertThat(memberInfo.myRole()).isEqualTo(member.getRole());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("멘토링 이미지가 없는 멘토는 로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfoForMentorWithoutImage() {
        // given
        Member member = FixtureUtil.testMentor();
        Mentoring mentoring = FixtureUtil.testMentoring(member);

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
            softAssertions.assertThat(memberInfo.myRole()).isEqualTo(member.getRole());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("멘토링 이미지가 있어도 회원 프로필 이미지가 없으면 내 정보 조회 시 이미지를 반환하지 않는다.")
    @Test
    void successGetMyInfoForMentorWithOnlyMentoringImage() {
        // given
        Member member = FixtureUtil.testMentee();
        Mentoring mentoring = FixtureUtil.testMentoring(member);

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
            softAssertions.assertThat(memberInfo.image()).isNull();
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(member.getLoginId());
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(member.getGender());
            softAssertions.assertThat(memberInfo.myRole()).isEqualTo(member.getRole());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("회원 프로필 이미지가 있으면 내 정보 조회 시 회원 프로필 이미지를 반환한다.")
    @Test
    void successGetMyInfoForMemberWithMemberProfileImage() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        Image image = imageRepository.save(Image.forKey(
                "fit-toring/local/member-profile-image/default/member-profile.jpg",
                ImageType.MEMBER_PROFILE,
                member.getId(),
                "member-profile"
        ));
        given(presignedUrlService.issueGetPresignedUrl(image.getKey()))
                .willReturn("https://presigned-get-member-profile-url");

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isEqualTo("https://presigned-get-member-profile-url");
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(member.getLoginId());
            softAssertions.assertThat(memberInfo.myRole()).isEqualTo(member.getRole());
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
        });
    }

    @DisplayName("멘티는 로그인 상태에서 내 요약 정보를 조회할 수 있다..")
    @Test
    void getMyInfoSummary() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());

        // when
        MyInfoSummaryResponse memberInfo = memberService.getMemberInfoSummary(member.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.name()).isEqualTo(member.getName());
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }

    @DisplayName("회원(멘토, 멘티)은 자신의 회원 정보인 이름, 성별, 비밀번호, 전화번호를 수정할 수 있다.")
    @Test
    void updateMemberInfo() {
        //given
        String rawName = "이름";
        Gender rawGender = Gender.MALE;
        String rawPhoneNumber = "010-1234-5678";
        Password password = Password.from("password");
        Member member = memberRepository.save(
                new Member(
                        "menteeId",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        password
                )
        );

        String newName = "newName";
        Gender newGender = Gender.FEMALE;
        String newPassword = "newPassword";
        String newPhoneNumber = "010-5678-9123";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                newGender,
                newPassword,
                newPhoneNumber,
                null
        );

        //when
        memberService.updateMemberInfo(member.getId(), request);

        //then
        Member actual = memberRepository.findById(member.getId())
                .orElse(null);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isNotNull();
            softly.assertThat(actual.getName()).isNotEqualTo(rawName);
            softly.assertThat(actual.getGender()).isNotEqualTo(rawGender);
            softly.assertThat(actual.getPassword()).isEqualTo(Password.from(newPassword));
            softly.assertThat(actual.getPhoneNumber()).isNotEqualTo(rawPhoneNumber);
        });
    }

    @DisplayName("회원(멘토, 멘티)은 자신의 이름, 성별, 비밀번호, 전화번호 중 일부를 선택적으로 수정할 수 있다.")
    @Test
    void updateMemberInfo2() {
        //given
        String rawName = "이름";
        Gender rawGender = Gender.MALE;
        String rawPhoneNumber = "010-1234-5678";
        Password password = Password.from("password");
        System.out.println(password.getValue());
        Member member = memberRepository.save(
                new Member(
                        "menteeId",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        password
                )
        );

        String newName = "newName";
        String newPhoneNumber = "010-5678-9123";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                null,
                null,
                newPhoneNumber,
                null
        );

        //when
        memberService.updateMemberInfo(member.getId(), request);

        //then
        Member actual = memberRepository.findById(member.getId())
                .orElse(null);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isNotNull();
            softly.assertThat(actual.getName()).isNotEqualTo(rawName);
            softly.assertThat(actual.getGender()).isEqualTo(rawGender);
            softly.assertThat(actual.getPasswordValue()).isEqualTo(password.getValue());
            softly.assertThat(actual.getPhoneNumber()).isNotEqualTo(rawPhoneNumber);
        });
    }

    @DisplayName("수정하려는 전화번호가 이미 사용중인 번호라면 예외가 발생한다.")
    @Test
    void duplicatePhoneNumber() {
        //given
        String rawName = "이름";
        Gender rawGender = Gender.MALE;
        String rawPhoneNumber = "010-1111-2222";
        Password rawPassword = Password.from("password");
        Member member = memberRepository.save(
                new Member(
                        "menteeId1",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        rawPassword
                )
        );

        Member testMentee = FixtureUtil.testMentee();
        memberRepository.save(testMentee);

        String newName = "newName";
        String newPhoneNumber = testMentee.getPhoneNumber();

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                null,
                null,
                newPhoneNumber,
                null
        );

        //when //then
        assertThatThrownBy(() -> memberService.updateMemberInfo(member.getId(), request))
                .isInstanceOf(DuplicatePhoneException.class)
                .hasMessage(BusinessErrorMessage.DUPLICATE_PHONE.getMessage());
    }

    @DisplayName("회원은 존재하는 S3 key로 프로필 이미지를 등록할 수 있다.")
    @Test
    void updateMemberProfileImage() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String profileImageKey = "fit-toring/local/member-profile-image/default/member-profile.jpg";
        given(presignedUrlService.isObjectExistsFromKey(profileImageKey)).willReturn(true);

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null,
                profileImageKey
        );

        // when
        memberService.updateMemberInfo(member.getId(), request);

        // then
        Image actual = imageRepository.findByImageTypeAndRelationIdAndImageVariant(
                        ImageType.MEMBER_PROFILE,
                        member.getId(),
                        ImageVariant.DEFAULT
                )
                .orElseThrow();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual.getKey()).isEqualTo(profileImageKey);
            softly.assertThat(actual.getUrl()).isNull();
            softly.assertThat(actual.getRelationId()).isEqualTo(member.getId());
        });
    }

    @DisplayName("회원은 프로필 이미지 key를 변경하면 기존 이미지가 교체된다.")
    @Test
    void replaceMemberProfileImage() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        imageRepository.save(Image.forKey(
                "fit-toring/local/member-profile-image/default/old-profile.jpg",
                ImageType.MEMBER_PROFILE,
                member.getId(),
                "old-profile"
        ));

        String newProfileImageKey = "fit-toring/local/member-profile-image/default/new-profile.jpg";
        given(presignedUrlService.isObjectExistsFromKey(newProfileImageKey)).willReturn(true);

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null,
                newProfileImageKey
        );

        // when
        memberService.updateMemberInfo(member.getId(), request);

        // then
        var images = imageRepository.findByImageTypeAndRelationIdIn(ImageType.MEMBER_PROFILE,
                java.util.List.of(member.getId()));

        assertThat(images).hasSize(1);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(images.getFirst().getKey()).isEqualTo(newProfileImageKey);
            softly.assertThat(images.getFirst().getUrl()).isNull();
        });
    }

    @DisplayName("회원은 빈 문자열을 보내 프로필 이미지를 삭제할 수 있다.")
    @Test
    void deleteMemberProfileImage() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        imageRepository.save(Image.forKey(
                "fit-toring/local/member-profile-image/default/member-profile.jpg",
                ImageType.MEMBER_PROFILE,
                member.getId(),
                "member-profile"
        ));

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null,
                ""
        );

        // when
        memberService.updateMemberInfo(member.getId(), request);

        // then
        assertThat(imageRepository.findByImageTypeAndRelationIdAndImageVariant(
                ImageType.MEMBER_PROFILE,
                member.getId(),
                ImageVariant.DEFAULT
        )).isEmpty();
    }

    @DisplayName("회원은 존재하지 않는 S3 key로 프로필 이미지를 저장할 수 없다.")
    @Test
    void updateMemberProfileImageWhenS3ObjectNotExists() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String profileImageKey = "fit-toring/local/member-profile-image/default/member-profile.jpg";
        given(presignedUrlService.isObjectExistsFromKey(profileImageKey)).willReturn(false);

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null,
                profileImageKey
        );

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberInfo(member.getId(), request))
                .isInstanceOf(InvalidImageKeyException.class)
                .hasMessage(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
    }

    @DisplayName("회원은 다른 이미지 타입의 S3 key로 프로필 이미지를 저장할 수 없다.")
    @Test
    void updateMemberProfileImageWithOtherImageTypeKey() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String certificateImageKey = "fit-toring/local/certificate-image/default/certificate.jpg";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null,
                certificateImageKey
        );

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberInfo(member.getId(), request))
                .isInstanceOf(InvalidImageKeyException.class)
                .hasMessage(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());

        verify(presignedUrlService, never()).isObjectExistsFromKey(certificateImageKey);
        assertThat(imageRepository.findByImageTypeAndRelationIdAndImageVariant(
                ImageType.MEMBER_PROFILE,
                member.getId(),
                ImageVariant.DEFAULT
        )).isEmpty();
    }

    @DisplayName("회원은 프로필 이미지 prefix 하위의 중첩 경로 key로 프로필 이미지를 저장할 수 없다.")
    @Test
    void updateMemberProfileImageWithNestedProfileImageKey() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String nestedProfileImageKey = "fit-toring/local/member-profile-image/default/nested/member-profile.jpg";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null,
                nestedProfileImageKey
        );

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberInfo(member.getId(), request))
                .isInstanceOf(InvalidImageKeyException.class)
                .hasMessage(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());

        verify(presignedUrlService, never()).isObjectExistsFromKey(nestedProfileImageKey);
        assertThat(imageRepository.findByImageTypeAndRelationIdAndImageVariant(
                ImageType.MEMBER_PROFILE,
                member.getId(),
                ImageVariant.DEFAULT
        )).isEmpty();
    }

    @DisplayName("요청 정보에 수정하려는 정보가 없는 경우 예외가 발생한다.")
    @Test
    void emptyRequestByUpdate() {
        //given
        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null,
                null
        );

        //when //then
        assertThatThrownBy(() -> memberService.updateMemberInfo(1L, request))
                .isInstanceOf(EmptyRequestException.class)
                .hasMessage(BusinessErrorMessage.EMPTY_REQUEST.getMessage());
    }
}
