package fittoring.mentoring.business.model.password;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.MisMatchPasswordException;
import fittoring.mentoring.business.exception.PasswordEncryptionException;
import fittoring.mentoring.infra.HexEncoder;
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
            return HexEncoder.convertHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordEncryptionException(BusinessErrorMessage.PASSWORD_ENCRYPTION_FAILED.getMessage());
        }
    }

    public void validateMatches(String password) {
        if (isNotMatches(password)) {
            throw new MisMatchPasswordException(BusinessErrorMessage.MIS_MATCH_PASSWORD.getMessage());
        }
    }

    private boolean isNotMatches(String password) {
        return !encrypt(password).equals(this.password);
    }
}
