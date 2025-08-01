package fittoring.mentoring.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexEncoderTest {

    @DisplayName("convertHex_바이트배열을_헥사문자열로_변환한다")
    @Test
    void convertHex() {
        byte[] input = new byte[]{(byte) 0xAF, (byte) 0x01, (byte) 0xFF};
        String result = HexEncoder.convertHex(input);

        assertThat(result).isEqualTo("af01ff");
    }

    @DisplayName("convertHex_빈배열은_빈문자열을_반환한다")
    @Test
    void convertHex2() {
        byte[] input = new byte[]{};
        String result = HexEncoder.convertHex(input);

        assertThat(result).isEmpty();
    }
}
