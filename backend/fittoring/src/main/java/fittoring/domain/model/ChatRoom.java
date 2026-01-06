package fittoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE chat_room SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "chat_room")
@Entity
public class ChatRoom {

    @EqualsAndHashCode.Include
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Getter
    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChatStatus status;

    @Getter
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Getter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ChatRoom(Long reservationId, Long menteeId, Long mentorId) {
        this(
                null,
                reservationId,
                menteeId,
                mentorId,
                null,
                ChatStatus.ACTIVATE,
                false,
                null
        );
    }

    public boolean isNonParticipant(Long memberId) {
        return !mentorId.equals(memberId) && !menteeId.equals(memberId);
    }

    public Long getOpponentIdOf(Long senderId) {
        if (Objects.equals(menteeId, senderId)) {
            return mentorId;
        }
        return menteeId;
    }
}
