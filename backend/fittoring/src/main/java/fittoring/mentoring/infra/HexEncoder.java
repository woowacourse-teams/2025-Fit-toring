package fittoring.mentoring.infra;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HexEncoder {

    public static String convertHex(byte[] rawHmac) {
        StringBuilder sb = new StringBuilder();
        for (byte byteData : rawHmac) {
            sb.append(String.format("%02x", byteData));
        }
        return sb.toString();
    }
}
