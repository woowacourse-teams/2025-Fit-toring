package fittoring.mentoring.business.service;

import fittoring.config.S3Configuration;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.infra.S3Uploader;
import fittoring.mentoring.presentation.dto.MyInfoResponse;
import fittoring.mentoring.presentation.dto.MyInfoSummaryResponse;
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
@Import({DbCleaner.class, MemberService.class, ImageService.class, S3Uploader.class, S3Configuration.class})
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
        String loginId = "loginId";
        String name = "사용자";
        String gender = "MALE";
        Phone phone = new Phone("010-1234-5678");
        Member member = new Member(
                loginId,
                gender,
                name,
                phone,
                Password.from("password")
        );
        Member savedMember = em.persist(member);

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(savedMember.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isNull();
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(loginId);
            softAssertions.assertThat(memberInfo.name()).isEqualTo(name);
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(gender);
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(phone.getNumber());
        });
    }

    @DisplayName("멘토링 이미지가 없는 멘토는 로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfoForMentorWithoutImage() {
        // given
        String loginId = "loginId";
        String name = "사용자";
        String gender = "MALE";
        Phone phone = new Phone("010-1234-5678");
        Member member = new Member(
                loginId,
                gender,
                name,
                phone,
                Password.from("password")
        );
        member.registerAsMentor();
        Member savedMember = em.persist(member);
        Mentoring mentoring = new Mentoring(
                member,
                2000,
                3,
                "content",
                "introduction"
        );
        em.persist(mentoring);

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(savedMember.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isNull();
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(loginId);
            softAssertions.assertThat(memberInfo.name()).isEqualTo(name);
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(gender);
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(phone.getNumber());
        });
    }

    @DisplayName("멘토링 이미지가 있는 멘토는 로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfoForMentorWithImage() {
        // given
        String loginId = "loginId";
        String name = "사용자";
        String gender = "MALE";
        Phone phone = new Phone("010-1234-5678");
        Member member = new Member(
                loginId,
                gender,
                name,
                phone,
                Password.from("password")
        );
        member.registerAsMentor();
        Member savedMember = em.persist(member);
        Mentoring mentoring = new Mentoring(
                member,
                2000,
                3,
                "content",
                "introduction"
        );
        em.persist(mentoring);
        Image image = new Image(
                "imageUrl",
                ImageType.MENTORING_PROFILE,
                mentoring.getId()
        );
        em.persist(image);

        // when
        MyInfoResponse memberInfo = memberService.getMemberInfo(savedMember.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.image()).isEqualTo(image.getUrl());
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(loginId);
            softAssertions.assertThat(memberInfo.name()).isEqualTo(name);
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(gender);
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(phone.getNumber());
        });
    }

    @DisplayName("멘티는 로그인 상태에서 내 요약 정보를 조회할 수 있다..")
    @Test
    void getMyInfoSummary() {
        // given
        String loginId = "loginId";
        String name = "사용자";
        String gender = "MALE";
        Phone phone = new Phone("010-1234-5678");
        Member member = new Member(
                loginId,
                gender,
                name,
                phone,
                Password.from("password")
        );
        Member savedMember = em.persist(member);

        // when
        MyInfoSummaryResponse memberInfo = memberService.getMemberInfoSummary(savedMember.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.name()).isEqualTo(name);
            softAssertions.assertThat(memberInfo.phoneNumber()).isEqualTo(phone.getNumber());
        });
    }
}
