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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class SmsRestClientService {

    public static final String FROM_PHONE = "010-4736-7769";
    public static final String RESERVATION_SUBJECT = "핏토링 예약 알림";

    private final RestClient smsRestClient;

    public void sendSms(String to, String text) throws NoSuchAlgorithmException, InvalidKeyException {
        String body = smsRestClient.post()
                .uri("/messages/v4/send-many/detail")
                .header("Authorization", createAuthorization())
                .body(Map.of("messages", List.of(new SmsSendClientDto(to, FROM_PHONE, text, RESERVATION_SUBJECT))))
                .retrieve()
                .body(String.class);
        System.out.println("client: " + body);
    }

    private String createAuthorization() throws NoSuchAlgorithmException, InvalidKeyException {
        String authenticationMethod = "HMAC-SHA256";
        String authenticationMethodForHash = "HmacSHA256";
        String apiKey = "NCSNNGK0GBIPV2JE";
        String apiSecret = "H85VCH3VQYJ60VIKEEXQKSNSY9N6KVKX";
        String now = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        String salt = UUID.randomUUID().toString();
        String data = now + salt;
        String signature = createSignature(authenticationMethodForHash, apiSecret, data);

        String header = String.format(
                "%s apiKey=%s, date=%s, salt=%s, signature=%s",
                authenticationMethod,
                apiKey,
                now,
                salt,
                signature
        );
        System.out.println("header: " + header);
        return header;
    }

    private String createSignature(String authenticationMethod, String apiKey, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKey = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), authenticationMethod);
        Mac mac = Mac.getInstance(authenticationMethod);
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(rawHmac.length * 2);
        for (byte b : rawHmac) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
