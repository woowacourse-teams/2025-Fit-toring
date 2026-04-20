package fittoring.application.community.service;

import fittoring.application.auth.CookieProvider;
import fittoring.infrastructure.HexEncoder;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
        return PostLikeActorId.from(actorId)
                .map(this::hash)
                .orElse(null);
    }

    public String resolveOrCreate(String actorId, HttpServletResponse response) {
        PostLikeActorId postLikeActorId = PostLikeActorId.from(actorId)
                .orElseGet(() -> createAndWriteActorCookie(response));
        return hash(postLikeActorId);
    }

    private PostLikeActorId createAndWriteActorCookie(HttpServletResponse response) {
        PostLikeActorId postLikeActorId = PostLikeActorId.create();
        writeActorCookie(postLikeActorId, response);
        return postLikeActorId;
    }

    private void writeActorCookie(PostLikeActorId postLikeActorId, HttpServletResponse response) {
        ResponseCookie cookie = cookieProvider.createCookieWithMaxAge(COOKIE_NAME, postLikeActorId.value(),
                COOKIE_MAX_AGE);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String hash(PostLikeActorId postLikeActorId) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(actorSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            return HexEncoder.convertHex(mac.doFinal(postLikeActorId.value().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("해시 계산 중 예외가 발생했습니다.", e);
        }
    }
}
