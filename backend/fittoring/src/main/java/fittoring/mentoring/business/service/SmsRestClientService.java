package fittoring.mentoring.business.service;

import fittoring.mentoring.business.service.dto.SmsSendClientDto;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class SmsRestClientService {

    public static final String RESERVATION_SUBJECT = "핏토링 예약 알림";
    public static final String SEND_MESSAGE_ENDPOINT = "/messages/v4/send-many/detail";

    private final RestClient smsRestClient;

    @Value("${SMS_FROM_PHONE}")
    public String fromPhone;

    @Value("${COOL_SMS_HMAC_HEADER}")
    private String authenticationMethod;

    @Value("${COOL_SMS_HMAC_HASH}")
    private String authenticationMethodForHash;

    @Value("${COOL_SMS_API_KEY}")
    private String apiKey;

    @Value("${COOL_SMS_SECRET_KEY}")
    private String apiSecret;

    public void sendSms(String to, String text) throws NoSuchAlgorithmException, InvalidKeyException {
        String body = smsRestClient.post()
                .uri(SEND_MESSAGE_ENDPOINT)
                .header("Authorization", createAuthorization())
                .body(Map.of("messages", List.of(new SmsSendClientDto(
                        to,
                        fromPhone,
                        text,
                        RESERVATION_SUBJECT
                ))))
                .retrieve()
                .body(String.class);
        System.out.println("client: " + body);
    }

    private String createAuthorization() throws NoSuchAlgorithmException, InvalidKeyException {
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

    private String createSignature(String authenticationMethod, String apiKey, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKey = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), authenticationMethod);
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
