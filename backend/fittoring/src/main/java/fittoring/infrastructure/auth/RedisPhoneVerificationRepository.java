package fittoring.infrastructure.auth;

import fittoring.application.auth.repository.PhoneVerificationData;
import fittoring.application.auth.repository.PhoneVerificationRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisPhoneVerificationRepository implements PhoneVerificationRepository {

    private static final String KEY_PREFIX = "phone:verify:";
    private static final String ATTEMPTS_SUFFIX = ":attempts";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_VERIFIED = "verified";

    private static final DefaultRedisScript<Long> INCREMENT_ATTEMPTS_SCRIPT;

    static {
        INCREMENT_ATTEMPTS_SCRIPT = new DefaultRedisScript<>();
        INCREMENT_ATTEMPTS_SCRIPT.setScriptText("""
                if redis.call('EXISTS', KEYS[1]) == 0 then
                    return -1
                end
                local count = redis.call('INCR', KEYS[2])
                if count == 1 then
                    local ttl = redis.call('TTL', KEYS[1])
                    if ttl > 0 then
                        redis.call('EXPIRE', KEYS[2], ttl)
                    end
                end
                return count
                """);
        INCREMENT_ATTEMPTS_SCRIPT.setResultType(Long.class);
    }

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String phoneNumber, String code, long ttlSeconds) {
        String key = key(phoneNumber);
        redisTemplate.opsForHash().put(key, FIELD_CODE, code);
        redisTemplate.opsForHash().put(key, FIELD_VERIFIED, "false");
        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<PhoneVerificationData> findByPhone(String phoneNumber) {
        String key = key(phoneNumber);
        String code = (String) redisTemplate.opsForHash().get(key, FIELD_CODE);
        if (code == null) {
            return Optional.empty();
        }
        String verified = (String) redisTemplate.opsForHash().get(key, FIELD_VERIFIED);
        return Optional.of(new PhoneVerificationData(
                phoneNumber,
                code,
                "true".equals(verified)
        ));
    }

    @Override
    public void markVerified(String phoneNumber) {
        String key = key(phoneNumber);
        redisTemplate.opsForHash().put(key, FIELD_VERIFIED, "true");
    }

    @Override
    public int incrementAttempts(String phoneNumber) {
        Long result = redisTemplate.execute(
                INCREMENT_ATTEMPTS_SCRIPT,
                List.of(key(phoneNumber), key(phoneNumber) + ATTEMPTS_SUFFIX)
        );
        return result == null ? -1 : result.intValue();
    }

    @Override
    public void delete(String phoneNumber) {
        redisTemplate.delete(key(phoneNumber));
        redisTemplate.delete(key(phoneNumber) + ATTEMPTS_SUFFIX);
    }

    private String key(String phoneNumber) {
        return KEY_PREFIX + phoneNumber;
    }
}
