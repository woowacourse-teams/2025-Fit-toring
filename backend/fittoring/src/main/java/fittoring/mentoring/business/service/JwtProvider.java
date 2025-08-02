package fittoring.mentoring.business.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey secretKey;

    public JwtProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(Long memberId) {
        return Jwts.builder()
                .setSubject(memberId.toString())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
