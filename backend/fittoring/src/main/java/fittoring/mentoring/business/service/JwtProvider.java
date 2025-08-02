package fittoring.mentoring.business.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
}
