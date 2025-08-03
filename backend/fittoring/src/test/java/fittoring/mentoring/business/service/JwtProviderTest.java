package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String secretKey = "my-test-secret-key-my-test-secret-key";
        long accessExpirationMillis = 1000L; //1초
        long refreshExpirationMillis = 2000L; //2초
        jwtProvider = new JwtProvider(secretKey, accessExpirationMillis, refreshExpirationMillis);
    }

    @DisplayName("토큰을 발급할 수 있다.")
    @Test
    void createAccessToken() {
        //given
        Long memberId = 1L;

        //when
        String actual = jwtProvider.createAccessToken(memberId);

        //then
        assertThat(actual).isNotNull();
    }

    @DisplayName("만료되지 않은 토큰에서 subject를 추출할 수 있다.")
    @Test
    void getSubjectFromPayloadBy() throws InterruptedException {
        //given
        Long memberId = 1L;
        String token = jwtProvider.createAccessToken(memberId);

        Thread.sleep(100); //0.1초 대기

        //when
        Long actual = jwtProvider.getSubjectFromPayloadBy(token);

        //then
        assertThat(actual).isEqualTo(memberId);
    }

    @DisplayName("만료된 토큰 에서 subject를 추출하려고 하면 예외가 발생한다.")
    @Test
    void getSubjectFromPayloadBy2() throws InterruptedException {
        //given
        Long memberId = 1L;
        String token = jwtProvider.createAccessToken(memberId);

        Thread.sleep(2000); // 2초 대기

        //when
        //then
        assertThatThrownBy(() -> jwtProvider.getSubjectFromPayloadBy(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("refresh 토큰을 발급할 수 있다.")
    @Test
    void createRefreshToken() {
        //given
        //when
        String refreshToken = jwtProvider.createRefreshToken();

        //then
        assertThat(refreshToken).isNotNull();
    }
}
