package fittoring.infrastructure.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RedisChatImageUploadTicketRepositoryTest {

    private static final String UPLOAD_ID = "upload-id";
    private static final String REDIS_KEY = "chat-image-upload:" + UPLOAD_ID;
    private static final Long MEMBER_ID = 7L;
    private static final Long CHAT_ROOM_ID = 1L;
    private static final String S3_KEY = "fit-toring/prod/chat-image/default/image.png";

    private final StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    private final RedisChatImageUploadTicketRepository repository =
            new RedisChatImageUploadTicketRepository(redisTemplate);

    RedisChatImageUploadTicketRepositoryTest() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Nested
    @DisplayName("create 메서드는")
    class Create {

        @Test
        @DisplayName("uploadId에 회원, 채팅방, S3 key를 TTL과 함께 저장한다")
        void savesTicketWithTtl() {
            Duration ttl = Duration.ofMinutes(5);

            repository.create(UPLOAD_ID, MEMBER_ID, CHAT_ROOM_ID, S3_KEY, ttl);

            then(valueOperations).should().set(
                    REDIS_KEY,
                    MEMBER_ID + ":" + CHAT_ROOM_ID + ":" + S3_KEY,
                    ttl
            );
        }
    }

    @Nested
    @DisplayName("consume 메서드는")
    class Consume {

        @Test
        @DisplayName("회원과 채팅방이 일치하면 S3 key를 반환하고 티켓을 삭제한다")
        void returnsS3KeyAndDeletesTicket() {
            given(valueOperations.get(REDIS_KEY))
                    .willReturn(MEMBER_ID + ":" + CHAT_ROOM_ID + ":" + S3_KEY);

            Optional<String> result = repository.consume(UPLOAD_ID, MEMBER_ID, CHAT_ROOM_ID);

            assertThat(result).contains(S3_KEY);
            then(redisTemplate).should().delete(REDIS_KEY);
        }

        @Test
        @DisplayName("티켓이 없거나 만료되었으면 빈 결과를 반환한다")
        void returnsEmptyWhenTicketDoesNotExist() {
            given(valueOperations.get(REDIS_KEY)).willReturn(null);

            Optional<String> result = repository.consume(UPLOAD_ID, MEMBER_ID, CHAT_ROOM_ID);

            assertThat(result).isEmpty();
            then(redisTemplate).should(never()).delete(REDIS_KEY);
        }

        @Test
        @DisplayName("다른 회원의 티켓이면 빈 결과를 반환하고 삭제하지 않는다")
        void rejectsDifferentMember() {
            given(valueOperations.get(REDIS_KEY))
                    .willReturn("999:" + CHAT_ROOM_ID + ":" + S3_KEY);

            Optional<String> result = repository.consume(UPLOAD_ID, MEMBER_ID, CHAT_ROOM_ID);

            assertThat(result).isEmpty();
            then(redisTemplate).should(never()).delete(REDIS_KEY);
        }

        @Test
        @DisplayName("다른 채팅방의 티켓이면 빈 결과를 반환하고 삭제하지 않는다")
        void rejectsDifferentChatRoom() {
            given(valueOperations.get(REDIS_KEY))
                    .willReturn(MEMBER_ID + ":999:" + S3_KEY);

            Optional<String> result = repository.consume(UPLOAD_ID, MEMBER_ID, CHAT_ROOM_ID);

            assertThat(result).isEmpty();
            then(redisTemplate).should(never()).delete(REDIS_KEY);
        }

        @Test
        @DisplayName("이미 소비한 uploadId를 다시 사용하면 빈 결과를 반환한다")
        void rejectsAlreadyConsumedUploadId() {
            given(valueOperations.get(REDIS_KEY))
                    .willReturn(MEMBER_ID + ":" + CHAT_ROOM_ID + ":" + S3_KEY)
                    .willReturn(null);

            Optional<String> firstResult = repository.consume(UPLOAD_ID, MEMBER_ID, CHAT_ROOM_ID);
            Optional<String> secondResult = repository.consume(UPLOAD_ID, MEMBER_ID, CHAT_ROOM_ID);

            assertThat(firstResult).contains(S3_KEY);
            assertThat(secondResult).isEmpty();
            then(redisTemplate).should(times(1)).delete(REDIS_KEY);
        }
    }
}
