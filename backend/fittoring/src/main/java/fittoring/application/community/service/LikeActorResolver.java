package fittoring.application.community.service;

import fittoring.application.auth.CookieProvider;
import fittoring.domain.model.LikeActorKeyHash;
import fittoring.infrastructure.HexEncoder;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class LikeActorResolver {

    public static final String COOKIE_NAME = "likeActorId";

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Duration COOKIE_MAX_AGE = Duration.ofDays(365);

    private final CookieProvider cookieProvider;
    private final String actorSecret;

    public LikeActorResolver(
            CookieProvider cookieProvider,
            @Value("${post-like.actor-secret}") String actorSecret
    ) {
        if (actorSecret == null || actorSecret.isBlank()) {
            throw new IllegalStateException("post-like.actor-secret 설정은 비어 있을 수 없습니다.");
        }
        this.cookieProvider = cookieProvider;
        this.actorSecret = actorSecret;
    }

    public LikeActorKeyHash resolve(String actorId) {
        return LikeActorId.from(actorId)
                .map(this::hash)
                .orElse(null);
    }

    public LikeActorKeyHash resolveOrCreate(String actorId, HttpServletResponse response) {
        LikeActorId likeActorId = LikeActorId.from(actorId)
                .orElseGet(() -> createAndWriteActorCookie(response));
        return hash(likeActorId);
    }

    private LikeActorId createAndWriteActorCookie(HttpServletResponse response) {
        LikeActorId likeActorId = LikeActorId.create();
        writeActorCookie(likeActorId, response);
        return likeActorId;
    }

    private void writeActorCookie(LikeActorId likeActorId, HttpServletResponse response) {
        ResponseCookie cookie = cookieProvider.createCookieWithMaxAge(COOKIE_NAME, likeActorId.value(),
                COOKIE_MAX_AGE);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private LikeActorKeyHash hash(LikeActorId likeActorId) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(actorSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            String hash = HexEncoder.convertHex(mac.doFinal(likeActorId.value().getBytes(StandardCharsets.UTF_8)));
            return new LikeActorKeyHash(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("해시 계산 중 예외가 발생했습니다.", e);
        }
    }
}
