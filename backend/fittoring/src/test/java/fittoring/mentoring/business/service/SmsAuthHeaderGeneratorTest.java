package fittoring.mentoring.business.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SmsAuthHeaderGeneratorTest {

    private final SmsAuthHeaderGenerator generator = new SmsAuthHeaderGenerator(
            "HMAC-SHA256",
            "HmacSHA256",
            "TEST_API_KEY",
            "TEST_SECRET_KEY"
    );

    @DisplayName("시그니처를 올바르게 생성한다.")
    @Test
    void createSignature() throws NoSuchAlgorithmException, InvalidKeyException {
        // given
        String data = "2025-07-21T12:00:00Z123e4567-e89b-12d3-a456-426614174000";

        // when
        String signature = generator.createSignature(data);

        // then
        String expected = "f0194e794feb4fa884330b6a5802b69cdefe42d739fa9d6058a008daff0424bf";
        assertThat(signature).isEqualTo(expected);
    }

    @DisplayName("헤더를 올바르게 생성한다.")
    @Test
    void createAuthHeader() throws NoSuchAlgorithmException, InvalidKeyException {
        // given
        // when
        String header = generator.createAuthorization();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
                    softAssertions.assertThat(header).startsWith("HMAC-SHA256 apiKey=");
                    softAssertions.assertThat(header).contains("date=");
                    softAssertions.assertThat(header).contains("salt=");
                    softAssertions.assertThat(header).contains("signature=");
                }
        );
    }
}
