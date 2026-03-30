package fittoring.infrastructure.auth;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.application.auth.repository.PhoneVerificationData;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataRedisTest
@Import(RedisPhoneVerificationRepository.class)
class RedisPhoneVerificationRepositoryTest {

    @Autowired
    private RedisPhoneVerificationRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Nested
    @DisplayName("save 메서드는")
    class Save {

        @Test
        @DisplayName("인증 코드를 저장하고 조회할 수 있다")
        void savesAndFinds() {
            repository.save("010-1234-5678", "123456", 180);

            Optional<PhoneVerificationData> result = repository.findByPhone("010-1234-5678");

            assertThat(result).isPresent();
            assertThat(result.get().code()).isEqualTo("123456");
            assertThat(result.get().verified()).isFalse();
        }

        @Test
        @DisplayName("같은 번호로 다시 저장하면 코드가 갱신된다")
        void overwritesOnSamePhone() {
            repository.save("010-1234-5678", "111111", 180);
            repository.save("010-1234-5678", "222222", 180);

            Optional<PhoneVerificationData> result = repository.findByPhone("010-1234-5678");

            assertThat(result.get().code()).isEqualTo("222222");
            assertThat(result.get().verified()).isFalse();
        }
    }

    @Nested
    @DisplayName("findByPhone 메서드는")
    class FindByPhone {

        @Test
        @DisplayName("존재하지 않는 번호를 조회하면 빈 결과를 반환한다")
        void returnsEmptyForNonExistent() {
            Optional<PhoneVerificationData> result = repository.findByPhone("010-0000-0000");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("TTL 만료 후 조회하면 빈 결과를 반환한다")
        void returnsEmptyAfterTtlExpiry() throws InterruptedException {
            repository.save("010-1234-5678", "123456", 1);

            Thread.sleep(1500);

            Optional<PhoneVerificationData> result = repository.findByPhone("010-1234-5678");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("markVerified 메서드는")
    class MarkVerified {

        @Test
        @DisplayName("인증 상태를 true로 변경한다")
        void marksAsVerified() {
            repository.save("010-1234-5678", "123456", 180);

            repository.markVerified("010-1234-5678");

            Optional<PhoneVerificationData> result = repository.findByPhone("010-1234-5678");
            assertThat(result.get().verified()).isTrue();
        }
    }

    @Nested
    @DisplayName("incrementAttempts 메서드는")
    class IncrementAttempts {

        @Test
        @DisplayName("시도 횟수를 1씩 증가시킨다")
        void incrementsCount() {
            repository.save("010-1234-5678", "123456", 180);

            assertThat(repository.incrementAttempts("010-1234-5678")).isEqualTo(1);
            assertThat(repository.incrementAttempts("010-1234-5678")).isEqualTo(2);
            assertThat(repository.incrementAttempts("010-1234-5678")).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("delete 메서드는")
    class Delete {

        @Test
        @DisplayName("인증 데이터와 시도 횟수를 모두 삭제한다")
        void deletesAllRelatedKeys() {
            repository.save("010-1234-5678", "123456", 180);
            repository.incrementAttempts("010-1234-5678");

            repository.delete("010-1234-5678");

            assertThat(repository.findByPhone("010-1234-5678")).isEmpty();
        }
    }
}
