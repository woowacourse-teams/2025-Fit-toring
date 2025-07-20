package fittoring.mentoring.business.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class SmsAuthHeaderGeneratorTest {

    private final SmsAuthHeaderGenerator generator;

    @DisplayName("시그니처를 올바르게 생성한다.")
    @Test
    void createSignature() throws NoSuchAlgorithmException, InvalidKeyException {
        // given
        String algorithm = "HmacSHA256";
        String secretKey = "TEST_SECRET";
        String data = "2025-07-21T12:00:00Z123e4567-e89b-12d3-a456-426614174000";

        // when
        String signature = generator.createSignature(algorithm, secretKey, data);

        // then
        String expected = "8ada524b84ab824b9e1e26bec8394d63bfc99a204dfebe9e0444425dc809c875";
        assertThat(signature).isEqualTo(expected);
    }

}