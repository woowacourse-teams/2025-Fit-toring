package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.exception.DuplicateLoginIdException;
import fittoring.mentoring.business.exception.MisMatchPasswordException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.RefreshToken;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.presentation.dto.AuthTokenResponse;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import fittoring.util.DbCleaner;
import java.time.LocalDateTime;
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
@Import({DbCleaner.class, AuthService.class, JwtProvider.class})
@DataJpaTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JwtProvider jwtProvider;

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
                new Phone("010-1234-5678"),
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
                new Phone("010-1234-5678"),
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
                new Phone("010-1234-5678"),
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

    @DisplayName("잘못된 비밀번호로 로그인에 실패하면 예외가 발생한다.")
    @Test
    void login2() {
        //given
        Member member = new Member(
                "loginId",
                "이름",
                "남",
                new Phone("010-1234-5678"),
                Password.from("password")
        );
        em.persist(member);

        String loginId = "loginId";
        String password = "wongPassword";

        //when
        //then
        assertThatThrownBy(() -> authService.login(loginId, password))
                .isInstanceOf(MisMatchPasswordException.class);
    }

    @DisplayName("정상적인 로그인이 성공하면 토큰을 반환한다.")
    @Test
    void login3() {
        //given
        Member member = new Member(
                "loginId",
                "이름",
                "남",
                new Phone("010-1234-5678"),
                Password.from("password")
        );
        Member savedMember = em.persist(member);

        String loginId = "loginId";
        String password = "password";

        //when
        AuthTokenResponse actual = authService.login(loginId, password);

        //then
        RefreshToken refreshToken = em.find(RefreshToken.class, 1L);
        SoftAssertions.assertSoftly(softly -> {
                    assertThat(actual.accessToken()).isNotNull();
                    assertThat(actual.refreshToken()).isNotNull();
                    assertThat(refreshToken).isNotNull();
                    assertThat(refreshToken.getMemberId()).isEqualTo(savedMember.getId());
                    assertThat(refreshToken.getTokenValue()).isEqualTo(actual.refreshToken());
                }
        );
    }

    @DisplayName("refreshToken을 이용해 accessToken과 refreshToken을 재발급 할 수 있다.")
    @Test
    void reissue() {
        //given
        String accessToken = jwtProvider.createAccessToken(1L);
        String refreshToken = jwtProvider.createRefreshToken();

        RefreshToken savedRefreshToken = new RefreshToken(
                1L,
                refreshToken,
                LocalDateTime.now().minusDays(1)
        );

        em.persist(savedRefreshToken);

        //when
        AuthTokenResponse actual = authService.reissue(refreshToken);

        //then
        RefreshToken newRefreshToken = em.find(RefreshToken.class, 1L);

        SoftAssertions.assertSoftly(softly -> {
                    assertThat(actual.accessToken()).isNotNull();
                    assertThat(actual.refreshToken()).isNotNull();
                    assertThat(actual.accessToken()).isNotEqualTo(accessToken);
                    assertThat(actual.refreshToken()).isNotEqualTo(refreshToken);
                    assertThat(newRefreshToken.getTokenValue()).isEqualTo(actual.refreshToken());
                    assertThat(newRefreshToken.getMemberId()).isEqualTo(savedRefreshToken.getMemberId());
                    assertThat(newRefreshToken.getCreateAt()).isAfterOrEqualTo(savedRefreshToken.getCreateAt());
                }
        );
    }
}
