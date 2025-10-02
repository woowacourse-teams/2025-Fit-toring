package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE chat_message SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "chat_message")
@Entity
public class ChatMessage {

    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Getter
    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Getter
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Getter
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Getter
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ChatMessage(
            Long chatRoomId,
            Long senderId,
            String content,
            LocalDateTime createdAt,
            boolean isDeleted,
            LocalDateTime deletedAt
    ) {
        this(null, chatRoomId, senderId, content, createdAt, isDeleted, deletedAt);
    }

    public ChatMessage(Long chatRoomId, Long senderId, String content) {
        this(null, chatRoomId, senderId, content, null, false, null);
    }
}
