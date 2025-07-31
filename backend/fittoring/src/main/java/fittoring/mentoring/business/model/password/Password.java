package fittoring.mentoring.business.model.password;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.PasswordEncryptionException;
import jakarta.persistence.Embeddable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
public class Password {

    private final String password;

    protected Password() {
        this.password = null;
    }

    public static Password from(String password) {
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
            throw new PasswordEncryptionException(BusinessErrorMessage.PASSWORD_ENCRYPTION_FAILED.getMessage());
        }
    }
}
