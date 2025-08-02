package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private static final String SECRET_KEY = "my-test-secret-key-my-test-secret-key";

    private final JwtProvider jwtProvider = new JwtProvider(SECRET_KEY);

    @DisplayName("memberId로 토큰을 발급한다.")
    @Test
    void createToken() {
        //given
        Long memberId = 1L;

        //when
        String actual = jwtProvider.createToken(memberId);

        //then
        assertThat(actual).isNotNull();
    }

}
