package fittoring.config;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SmsRestClientConfiguration {

    @Bean
    public RestClient restClient(@Autowired RestClient.Builder restClientBuilder) {
        return restClientBuilder.baseUrl("https://api.solapi.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String createSignature(String authenticationMethod, String apiKey, String data)
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

    public String createAuthorization() throws NoSuchAlgorithmException, InvalidKeyException {
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
}
