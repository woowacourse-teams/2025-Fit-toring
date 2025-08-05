package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.presentation.dto.MyInfoResponse;
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
@Import({DbCleaner.class, MemberService.class})
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

    @DisplayName("로그인 상태에서 내 정보를 조회할 수 있다.")
    @Test
    void successGetMyInfo() {
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
        MyInfoResponse memberInfo = memberService.getMeInfo(savedMember.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberInfo.loginId()).isEqualTo(loginId);
            softAssertions.assertThat(memberInfo.name()).isEqualTo(name);
            softAssertions.assertThat(memberInfo.gender()).isEqualTo(gender);
            softAssertions.assertThat(memberInfo.phone()).isEqualTo(phone.getNumber());
        });
    }
}