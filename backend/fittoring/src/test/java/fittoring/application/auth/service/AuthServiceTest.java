package fittoring.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.FixtureUtil;
import fittoring.application.auth.presentation.dto.request.SignUpRequest;
import fittoring.application.auth.presentation.dto.response.AuthTokenResponse;
import fittoring.application.auth.presentation.dto.response.LoginResponse;
import fittoring.application.exception.DuplicateLoginIdException;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Member;
import fittoring.domain.model.RefreshToken;
import fittoring.infrastructure.OauthClientService;
import fittoring.util.DbCleaner;
import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, AuthService.class, JwtProvider.class, QueryDslConfig.class, OauthClientService.class,
        MentoringPaginationHelper.class})
@ExtendWith(MockitoExtension.class)
@DataJpaTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockitoBean
    OauthClientService oauthClientService;

    @MockitoBean
    RestClient restClient;

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
                "MALE",
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
        Member mentee = em.persist(FixtureUtil.getTestMentee());

        String loginId = mentee.getLoginId();

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
        em.persist(FixtureUtil.getTestMentee());

        String loginId = "nonDuplicateId";

        //when
        //then
        assertThatCode(() -> authService.validateDuplicateLoginId(loginId))
                .doesNotThrowAnyException();
    }

    @DisplayName("잘못된 아이디로 로그인에 실패하면 예외가 발생한다.")
    @Test
    void login() {
        //given
        em.persist(FixtureUtil.getTestMentee());

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
        em.persist(FixtureUtil.getTestMentee());

        String loginId = "menteeId";
        String password = "wongPassword";

        //when
        //then
        assertThatThrownBy(() -> authService.login(loginId, password))
                .isInstanceOf(MisMatchPasswordException.class);
    }

    @DisplayName("정상적인 로그인이 성공하면 member 식별자와 토큰을 반환한다.")
    @Test
    void login3() {
        //given
        Member savedMember = em.persist(FixtureUtil.getTestMentee());

        String loginId = savedMember.getLoginId();
        String rawPassword = "password";

        //when
        LoginResponse actual = authService.login(loginId, rawPassword);

        //then
        RefreshToken refreshToken = em.find(RefreshToken.class, savedMember.getId());
        SoftAssertions.assertSoftly(softly -> {
                    assertThat(actual.memberLoginResponse().memberId()).isEqualTo(savedMember.getId());
                    assertThat(actual.authToken().accessToken()).isNotNull();
                    assertThat(actual.authToken().refreshToken()).isNotNull();
                    assertThat(refreshToken).isNotNull();
                    assertThat(refreshToken.getMember().getId()).isEqualTo(savedMember.getId());
                    assertThat(refreshToken.getTokenValue()).isEqualTo(actual.authToken().refreshToken());
                }
        );
    }

    @DisplayName("refreshToken을 이용해 accessToken과 refreshToken을 재발급 할 수 있다.")
    @Test
    void reissue() {
        //given
        Member savedMember = em.persist(FixtureUtil.getTestMentee());
        em.flush();
        String accessToken = jwtProvider.createAccessToken(1L);
        String refreshToken = jwtProvider.createRefreshToken();

        RefreshToken savedRefreshToken = new RefreshToken(
                refreshToken, LocalDateTime.now().minusDays(1), savedMember
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
                    assertThat(newRefreshToken.getMember()).isEqualTo(savedRefreshToken.getMember());
                    assertThat(newRefreshToken.getCreateAt()).isAfterOrEqualTo(savedRefreshToken.getCreateAt());
                }
        );
    }

    @DisplayName("로그아웃이 성공하면 해당 사용자의 refreshToken이 db에서 제거된다.")
    @Test
    void logout() {
        //given
        Member savedMember = em.persist(FixtureUtil.getTestMentee());

        String refreshToken = jwtProvider.createRefreshToken();
        RefreshToken savedRefreshToken = em.persist(
                new RefreshToken(refreshToken, LocalDateTime.now(), savedMember)
        );

        //when
        authService.logout(savedMember.getId());
        em.flush();
        em.clear();

        //then
        RefreshToken refreshToken1 = em.find(RefreshToken.class, savedRefreshToken.getId());
        assertThat(refreshToken1).isNull();
    }

    @DisplayName("refreshToken이 존재하지 않아도 로그아웃은 멱등하게 처리된다(예외 없음).")
    @Test
    void logout2() {
        // given
        Long memberId = 123L; // 토큰이 존재하지 않는 임의의 회원 ID

        // when
        // then
        assertThatCode(() -> authService.logout(memberId))
                .doesNotThrowAnyException();
    }
}
