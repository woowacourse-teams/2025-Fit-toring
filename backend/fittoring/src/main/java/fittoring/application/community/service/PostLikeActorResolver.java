package fittoring.application.community.service;

import fittoring.application.auth.CookieProvider;
import fittoring.infrastructure.HexEncoder;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class PostLikeActorResolver {

    public static final String COOKIE_NAME = "postLikeActorId";

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Duration COOKIE_MAX_AGE = Duration.ofDays(365);

    private final CookieProvider cookieProvider;
    private final String actorSecret;

    public PostLikeActorResolver(
            CookieProvider cookieProvider,
            @Value("${post-like.actor-secret}") String actorSecret
    ) {
        this.cookieProvider = cookieProvider;
        this.actorSecret = actorSecret;
    }

    public String resolve(String actorId) {
        if (!isValidUuid(actorId)) {
            return null;
        }
        return hash(actorId);
    }

    public String resolveOrCreate(String actorId, HttpServletResponse response) {
        if (isValidUuid(actorId)) {
            return hash(actorId);
        }
        String newActorId = UUID.randomUUID().toString();
        writeActorCookie(newActorId, response);
        return hash(newActorId);
    }

    private boolean isValidUuid(String actorId) {
        if (actorId == null || actorId.isBlank()) {
            return false;
        }
        try {
            UUID.fromString(actorId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void writeActorCookie(String actorId, HttpServletResponse response) {
        ResponseCookie cookie = cookieProvider.createCookieWithMaxAge(COOKIE_NAME, actorId, COOKIE_MAX_AGE);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String hash(String actorId) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(actorSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            return HexEncoder.convertHex(mac.doFinal(actorId.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("해시 계산 중 예외가 발생했습니다.", e);
        }
    }
}
