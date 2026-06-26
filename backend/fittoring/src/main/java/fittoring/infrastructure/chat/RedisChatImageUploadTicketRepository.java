package fittoring.infrastructure.chat;

import fittoring.application.chat.repository.ChatImageUploadTicketRepository;
import fittoring.infrastructure.dto.ImageUploadTicket;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatImageUploadTicketRepository implements ChatImageUploadTicketRepository {

    private static final String KEY_PREFIX = "chat-image-upload:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void create(String uploadId, Long memberId, Long chatRoomId, String s3Key, Duration ttl) {
        redisTemplate.opsForValue().set(
                key(uploadId),
                ImageUploadTicket.of(memberId, chatRoomId, s3Key).serialize(),
                ttl
        );
    }

    @Override
    public Optional<String> consume(String uploadId, Long memberId, Long chatRoomId) {
        String redisKey = key(uploadId);

        ImageUploadTicket ticket = ImageUploadTicket.parse(redisTemplate.opsForValue().get(redisKey));
        if (ticket == null || !ticket.isOwnedBy(memberId, chatRoomId)) {
            return Optional.empty();
        }

        redisTemplate.delete(redisKey);
        return Optional.of(ticket.s3Key());
    }

    private String key(String uploadId) {
        return KEY_PREFIX + uploadId;
    }
}
