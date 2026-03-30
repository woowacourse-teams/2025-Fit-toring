package fittoring.infrastructure.auth;

import fittoring.application.auth.repository.RefreshTokenRepository;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String TOKEN_KEY_PREFIX = "refresh:";
    private static final String MEMBER_KEY_PREFIX = "refresh:member:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String tokenValue, Long memberId, long ttlMillis) {
        String tokenKey = tokenKey(tokenValue);
        String memberKey = memberKey(memberId);

        redisTemplate.opsForValue().set(tokenKey, String.valueOf(memberId), ttlMillis, TimeUnit.MILLISECONDS);
        redisTemplate.opsForSet().add(memberKey, tokenValue);
        redisTemplate.expire(memberKey, ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<Long> findMemberIdByTokenValue(String tokenValue) {
        String value = redisTemplate.opsForValue().get(tokenKey(tokenValue));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Long.valueOf(value));
    }

    @Override
    public void deleteByTokenValue(String tokenValue) {
        String tokenKey = tokenKey(tokenValue);
        String memberIdStr = redisTemplate.opsForValue().get(tokenKey);
        redisTemplate.delete(tokenKey);

        if (memberIdStr != null) {
            String memberKey = memberKey(Long.valueOf(memberIdStr));
            redisTemplate.opsForSet().remove(memberKey, tokenValue);
        }
    }

    @Override
    public void deleteAllByMemberId(Long memberId) {
        String memberKey = memberKey(memberId);
        Set<String> tokenValues = redisTemplate.opsForSet().members(memberKey);

        if (tokenValues != null && !tokenValues.isEmpty()) {
            for (String tokenValue : tokenValues) {
                redisTemplate.delete(tokenKey(tokenValue));
            }
        }
        redisTemplate.delete(memberKey);
    }

    private String tokenKey(String tokenValue) {
        return TOKEN_KEY_PREFIX + tokenValue;
    }

    private String memberKey(Long memberId) {
        return MEMBER_KEY_PREFIX + memberId;
    }
}
