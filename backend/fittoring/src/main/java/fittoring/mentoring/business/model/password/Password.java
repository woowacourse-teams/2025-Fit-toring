package fittoring.mentoring.business.model.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Password {

    private final String password;

    public static Password formEncrypt(String password) {
        return new Password(password);
    }

    public static Password createEncrypt(String password) {
        return new Password(encrypt(password));
    }

    private static String encrypt(String password) {
        try {
            final String algorithm = "SHA-256";
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = password.getBytes();
            byte[] digest = md.digest(bytes);

            StringBuilder hex = new StringBuilder();

            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("비밀번호 암호화에 실패했습니다.");
        }
    }
}
