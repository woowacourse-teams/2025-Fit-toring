package fittoring.mentoring.infra;

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

    @DisplayName("헤더를 올바르게 생성한다.")
    @Test
    void createAuthHeader() {
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
