package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessExpirationMillis;
    private final long refreshExpirationMillis;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration-millis}") long accessExpirationMillis,
            @Value("${jwt.refresh-token-expiration-millis}") long refreshExpirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    public String createToken(Long memberId) {
        Date now = new Date();
        Date accessMillis = new Date(now.getTime() + accessExpirationMillis);
        return buildToken(memberId.toString(), now, accessMillis);
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date refreshMillis = new Date(now.getTime() + refreshExpirationMillis);
        return buildToken(UUID.randomUUID().toString(), now, refreshMillis);
    }

    private String buildToken(String subject, Date issuedAt, Date expiresAt) {
        JwtBuilder builder = Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setIssuedAt(issuedAt)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expiresAt);

        if (subject != null) {
            builder.setSubject(subject);
        }
        return builder.compact();
    }

    public Long getSubjectFromPayloadBy(String token) {
        validateToken(token);
        return Long.valueOf(getSubject(token));
    }

    public void validateToken(String token) {
        try {
            getParse().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(BusinessErrorMessage.EXPIRED_TOKEN.getMessage());
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new InvalidTokenException(BusinessErrorMessage.INVALID_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(BusinessErrorMessage.EMPTY_TOKEN.getMessage());
        }
    }

    private String getSubject(String token) {
        return getParse()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    private JwtParser getParse() {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }
}
