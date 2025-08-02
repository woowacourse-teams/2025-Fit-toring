package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-millis}") long expirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationMillis = expirationMillis;
    }

    public String createToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(memberId.toString())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .compact();
    }

    public Long getSubjectFromPayloadBy(String token) {
        validateToken(token);
        return Long.valueOf(getSubject(token));
    }

    private void validateToken(String token) {
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
