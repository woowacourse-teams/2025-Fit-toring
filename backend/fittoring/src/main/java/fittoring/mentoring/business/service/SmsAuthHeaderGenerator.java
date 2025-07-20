package fittoring.mentoring.business.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsAuthHeaderGenerator {

    @Value("${COOL_SMS_HMAC_HEADER}")
    protected String authenticationMethod;

    @Value("${COOL_SMS_HMAC_HASH}")
    protected String authenticationMethodForHash;

    @Value("${COOL_SMS_API_KEY}")
    protected String apiKey;

    @Value("${COOL_SMS_SECRET_KEY}")
    protected String apiSecret;

    public String createAuthorization() throws NoSuchAlgorithmException, InvalidKeyException {
        String now = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        String salt = UUID.randomUUID().toString();
        String data = now + salt;
        String signature = createSignature(authenticationMethodForHash, apiSecret, data);

        return String.format(
                "%s apiKey=%s, date=%s, salt=%s, signature=%s",
                authenticationMethod,
                apiKey,
                now,
                salt,
                signature
        );
    }

    public String createSignature(String authenticationMethod, String apiSecretKey, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKey = new SecretKeySpec(
                apiSecretKey.getBytes(StandardCharsets.UTF_8),
                authenticationMethod
        );
        Mac mac = Mac.getInstance(authenticationMethod);
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return convertHex(rawHmac);
    }

    private String convertHex(byte[] rawHmac) {
        StringBuilder sb = new StringBuilder(rawHmac.length * 2);
        for (byte b : rawHmac) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
