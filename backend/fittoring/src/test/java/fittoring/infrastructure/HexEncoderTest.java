package fittoring.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexEncoderTest {

    @DisplayName("convertHex 바이트 배열을 헥사 문자열로 변환한다.")
    @Test
    void convertHex() {
        byte[] input = new byte[]{(byte) 0xAF, (byte) 0x01, (byte) 0xFF};
        String result = HexEncoder.convertHex(input);

        assertThat(result).isEqualTo("af01ff");
    }

    @DisplayName("convertHex 빈 배열은 빈 문자열을 반환한다.")
    @Test
    void convertHex2() {
        byte[] input = new byte[]{};
        String result = HexEncoder.convertHex(input);

        assertThat(result).isEmpty();
    }
}
