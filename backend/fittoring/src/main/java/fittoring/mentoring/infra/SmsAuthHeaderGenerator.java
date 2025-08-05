package fittoring.mentoring.infra;

import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.SmsException;
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

    private final String authenticationMethod;
    private final String authenticationMethodForHash;
    private final String apiKey;
    private final String apiSecret;

    public SmsAuthHeaderGenerator(
            @Value("${COOL_SMS_HMAC_HEADER}") String authenticationMethod,
            @Value("${COOL_SMS_HMAC_HASH}") String authenticationMethodForHash,
            @Value("${COOL_SMS_API_KEY}") String apiKey,
            @Value("${COOL_SMS_SECRET_KEY}") String apiSecret
    ) {
        this.authenticationMethod = authenticationMethod;
        this.authenticationMethodForHash = authenticationMethodForHash;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String createAuthorization() {
        String now = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        String salt = UUID.randomUUID().toString();
        String data = now + salt;
        String signature = createSignature(data);

        return String.format(
                "%s apiKey=%s, date=%s, salt=%s, signature=%s",
                authenticationMethod,
                apiKey,
                now,
                salt,
                signature
        );
    }

    private String createSignature(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                    apiSecret.getBytes(StandardCharsets.UTF_8),
                    authenticationMethodForHash
            );
            Mac mac = Mac.getInstance(authenticationMethodForHash);
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexEncoder.convertHex(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new SmsException(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
        }
    }
}
