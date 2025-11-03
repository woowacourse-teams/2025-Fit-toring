package fittoring.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.presentation.dto.request.OauthSignUpRequest;
import fittoring.application.auth.presentation.dto.request.SignUpRequest;
import fittoring.application.auth.repository.RefreshTokenRepository;
import fittoring.application.auth.service.dto.AuthTokenDto;
import fittoring.application.auth.service.dto.LoginInfoDto;
import fittoring.application.exception.DuplicateLoginIdException;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberOauth;
import fittoring.domain.model.RefreshToken;
import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
        String actual = memberRepository.findById(1L)
                .orElseThrow(null).getPassword();
        assertThat(actual).isNotEqualTo(password);
    }

    @DisplayName("중복된 id가 존재하면 예외가 발생한다.")
    @Test
    void validateDuplicateLoginId() {
        //given
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

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
        Member mentee = FixtureUtil.getTestMentee();
        memberRepository.save(mentee);

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
        Member mentee = FixtureUtil.getTestMentee();
        memberRepository.save(mentee);

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
        Member mentee = FixtureUtil.getTestMentee();
        memberRepository.save(mentee);

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
        Member mentee = FixtureUtil.getTestMentee();
        memberRepository.save(mentee);

        String loginId = mentee.getLoginId();
        String rawPassword = "password";

        //when
        LoginInfoDto actual = authService.login(loginId, rawPassword);

        //then
        RefreshToken refreshToken = refreshTokenRepository.findByTokenValue(actual.authTokenDto().refreshToken())
                .orElseThrow(null);
        SoftAssertions.assertSoftly(softly -> {
                    assertThat(actual.memberId()).isEqualTo(mentee.getId());
                    assertThat(actual.authTokenDto().accessToken()).isNotNull();
                    assertThat(actual.authTokenDto().refreshToken()).isNotNull();
                    assertThat(refreshToken).isNotNull();
                    assertThat(refreshToken.getMember().getId()).isEqualTo(mentee.getId());
                    assertThat(refreshToken.getTokenValue()).isEqualTo(actual.authTokenDto().refreshToken());
                }
        );
    }

    @DisplayName("refreshToken을 이용해 accessToken과 refreshToken을 재발급 할 수가 있다.")
    @Test
    void reissue() {
        //given
        Member savedMember = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(1L);
        String refreshToken = jwtProvider.createRefreshToken();

        RefreshToken savedRefreshToken = new RefreshToken(
                refreshToken, LocalDateTime.now().minusDays(1), savedMember
        );

        refreshTokenRepository.save(savedRefreshToken);

        //when
        AuthTokenDto actual = authService.reissue(refreshToken);

        //then
        RefreshToken newRefreshToken = refreshTokenRepository.findById(savedRefreshToken.getId())
                .orElse(null);

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
        Member savedMember = memberRepository.save(FixtureUtil.getTestMentee());

        String refreshToken = jwtProvider.createRefreshToken();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(
                new RefreshToken(refreshToken, LocalDateTime.now(), savedMember)
        );

        //when
        authService.logout(savedMember.getId());

        //then
        RefreshToken refreshToken1 = refreshTokenRepository.findById(savedRefreshToken.getId())
                .orElse(null);
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

    @DisplayName("oauth 회원가입이 가능하다.")
    @Test
    void registerOauthMember() {
        // given
        String phoneNumber = "010-1234-5678";
        OauthSignUpRequest request = new OauthSignUpRequest("이름", "MALE", phoneNumber);
        willReturn(1L).given(jwtProvider).getSubjectFromPayloadBy(any());

        // when
        MemberOauth memberOauth = authService.registerOauthMember(request, "validOauthSignUpToken");

        // then
        SoftAssertions.assertSoftly(softly -> {
                    assertThat(memberOauth).isNotNull();
                    assertThat(memberOauth.getMember().getPhoneNumber()).isEqualTo(phoneNumber);
                }
        );
    }

    @DisplayName("기존 회원도 oauth 회원가입이 가능하다.")
    @Test
    void registerOauthMember2() {
        // given
        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        OauthSignUpRequest request = new OauthSignUpRequest("이름", "MALE", mentee.getPhoneNumber());
        willReturn(1L).given(jwtProvider).getSubjectFromPayloadBy(any());

        // when
        MemberOauth memberOauth = authService.registerOauthMember(request, "validOauthSignUpToken");

        // then
        SoftAssertions.assertSoftly(softly -> {
                    assertThat(memberOauth).isNotNull();
                    assertThat(memberOauth.getMember().getPhoneNumber()).isEqualTo(mentee.getPhoneNumber());
                    assertThat(memberOauth.getMember().getName()).isEqualTo(mentee.getName());
                    assertThat(memberOauth.getMember().getLoginId()).isEqualTo(mentee.getLoginId());
                }
        );
    }
}
