package fittoring.infrastructure.auth;

import static org.assertj.core.api.Assertions.assertThat;

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
@Import(RedisRefreshTokenRepository.class)
class RedisRefreshTokenRepositoryTest {

    @Autowired
    private RedisRefreshTokenRepository repository;

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
        @DisplayName("토큰을 저장하고 memberId를 조회할 수 있다")
        void savesAndFinds() {
            repository.save("token-value-1", 1L, 604800000);

            Optional<Long> result = repository.findMemberIdByTokenValue("token-value-1");

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(1L);
        }

        @Test
        @DisplayName("같은 회원이 여러 토큰을 저장할 수 있다")
        void savesMultipleTokensPerMember() {
            repository.save("token-1", 1L, 604800000);
            repository.save("token-2", 1L, 604800000);

            assertThat(repository.findMemberIdByTokenValue("token-1")).isPresent();
            assertThat(repository.findMemberIdByTokenValue("token-2")).isPresent();
        }
    }

    @Nested
    @DisplayName("findMemberIdByTokenValue 메서드는")
    class FindMemberIdByTokenValue {

        @Test
        @DisplayName("존재하지 않는 토큰을 조회하면 빈 결과를 반환한다")
        void returnsEmptyForNonExistent() {
            Optional<Long> result = repository.findMemberIdByTokenValue("non-existent");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("TTL 만료 후 조회하면 빈 결과를 반환한다")
        void returnsEmptyAfterTtlExpiry() throws InterruptedException {
            repository.save("token-value-1", 1L, 1000);

            Thread.sleep(1500);

            assertThat(repository.findMemberIdByTokenValue("token-value-1")).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteByTokenValue 메서드는")
    class DeleteByTokenValue {

        @Test
        @DisplayName("해당 토큰을 삭제한다")
        void deletesToken() {
            repository.save("token-1", 1L, 604800000);

            repository.deleteByTokenValue("token-1");

            assertThat(repository.findMemberIdByTokenValue("token-1")).isEmpty();
        }

        @Test
        @DisplayName("같은 회원의 다른 토큰에는 영향이 없다")
        void doesNotAffectOtherTokens() {
            repository.save("token-1", 1L, 604800000);
            repository.save("token-2", 1L, 604800000);

            repository.deleteByTokenValue("token-1");

            assertThat(repository.findMemberIdByTokenValue("token-1")).isEmpty();
            assertThat(repository.findMemberIdByTokenValue("token-2")).isPresent();
        }
    }

    @Nested
    @DisplayName("deleteAllByMemberId 메서드는")
    class DeleteAllByMemberId {

        @Test
        @DisplayName("해당 회원의 모든 토큰을 삭제한다")
        void deletesAllTokensForMember() {
            repository.save("token-1", 1L, 604800000);
            repository.save("token-2", 1L, 604800000);
            repository.save("token-3", 2L, 604800000);

            repository.deleteAllByMemberId(1L);

            assertThat(repository.findMemberIdByTokenValue("token-1")).isEmpty();
            assertThat(repository.findMemberIdByTokenValue("token-2")).isEmpty();
            assertThat(repository.findMemberIdByTokenValue("token-3")).isPresent();
        }
    }
}
