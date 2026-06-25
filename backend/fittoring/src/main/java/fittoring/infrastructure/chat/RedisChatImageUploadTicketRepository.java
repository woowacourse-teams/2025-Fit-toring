package fittoring.infrastructure.chat;

import fittoring.application.chat.repository.ChatImageUploadTicketRepository;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatImageUploadTicketRepository implements ChatImageUploadTicketRepository {

    private static final String KEY_PREFIX = "chat-image-upload:";
    private static final String VALUE_DELIMITER = ":";
    private static final int TICKET_FIELD_COUNT = 3;

    private final StringRedisTemplate redisTemplate;

    @Override
    public void create(String uploadId, Long memberId, Long chatRoomId, String s3Key, Duration ttl) {
        redisTemplate.opsForValue().set(
                key(uploadId),
                value(memberId, chatRoomId, s3Key),
                ttl
        );
    }

    @Override
    public Optional<String> consume(String uploadId, Long memberId, Long chatRoomId) {
        String redisKey = key(uploadId);
        String storedValue = redisTemplate.opsForValue().get(redisKey);
        if (storedValue == null) {
            return Optional.empty();
        }

        String[] ticketFields = storedValue.split(VALUE_DELIMITER, TICKET_FIELD_COUNT);
        if (ticketFields.length != TICKET_FIELD_COUNT
                || !ticketFields[0].equals(String.valueOf(memberId))
                || !ticketFields[1].equals(String.valueOf(chatRoomId))) {
            return Optional.empty();
        }

        redisTemplate.delete(redisKey);
        return Optional.of(ticketFields[2]);
    }

    private String key(String uploadId) {
        return KEY_PREFIX + uploadId;
    }

    private String value(Long memberId, Long chatRoomId, String s3Key) {
        return memberId + VALUE_DELIMITER + chatRoomId + VALUE_DELIMITER + s3Key;
    }
}
