package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.exception.DuplicateLoginIdException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import fittoring.util.DbCleaner;
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
@Import({DbCleaner.class, AuthService.class})
@DataJpaTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("회원을 저장할 때 암호화된 비밀번호가 저장된다.")
    @Test
    void register() {
        //given
        String password = "password";
        SignUpRequest request = new SignUpRequest(
                "loginId",
                "이름",
                "남",
                "010-1234-5678",
                password);

        //when
        authService.register(request);

        //then
        String actual = em.find(Member.class, 1L).getPassword();
        assertThat(actual).isNotEqualTo(password);
    }

    @DisplayName("중복된 id가 존재하면 예외가 발생한다.")
    @Test
    void validateDuplicateLoginId() {
        //given
        String loginId = "loginId";

        Member member = new Member(
                loginId,
                "이름",
                "남",
                "010-1234-5678",
                Password.from("password")
        );
        em.persist(member);

        //when
        //then
        assertThatThrownBy(() -> authService.validateDuplicateLoginId(loginId))
                .isInstanceOf(DuplicateLoginIdException.class)
                .hasMessage("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
    }

    @DisplayName("중복된 id가 존재하지 않으면 정상동작 한다.")
    @Test
    void validateDuplicateLoginId2() {
        //given
        String loginId = "nonDuplicateId";

        Member member = new Member(
                "loginId",
                "이름",
                "남",
                "010-1234-5678",
                Password.from("password")
        );
        em.persist(member);

        //when
        //then
        assertThatCode(() -> authService.validateDuplicateLoginId(loginId))
                .doesNotThrowAnyException();
    }

    @DisplayName("잘못된 아이디로 로그인에 실패하면 예외가 발생한다.")
    @Test
    void login() {
        //given
        Member member = new Member(
                "loginId",
                "이름",
                "남",
                "010-1234-5678",
                Password.from("password")
        );
        em.persist(member);

        String loginId = "wrongLoginId";
        String password = "password";

        //when
        //then
        assertThatThrownBy(() -> authService.login(loginId, password))
                .isInstanceOf(NotFoundMemberException.class);
    }
}
