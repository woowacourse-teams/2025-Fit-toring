package fittoring.domain.model;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.domain.model.password.Password;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE post SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "post")
@Entity
public class Post {

    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "guest_password")
    private String guestPassword;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static Post forMember(Member member, String title, String content, boolean isAnonymous, String nickname) {
        return new Post(
                null,
                title,
                content,
                member,
                nickname,
                null,
                isAnonymous,
                null,
                false,
                null
        );
    }

    public static Post forGuest(String title, String content, String nickname, String rawPassword) {
        return new Post(
                null,
                title,
                content,
                null,
                nickname,
                encryptPassword(rawPassword),
                false,
                null,
                false,
                null
        );
    }

    public void modify(String title, String content) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }

    public boolean isOwnedBy(Long memberId) {
        return member != null && member.isSameIdWith(memberId);
    }

    public boolean isGuestPost() {
        return member == null;
    }

    public void matchGuestPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new MisMatchPasswordException(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
        }
        String encryptedPassword = encryptPassword(rawPassword);
        if (!encryptedPassword.equals(this.guestPassword)) {
            throw new MisMatchPasswordException(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
        }
    }

    private static String encryptPassword(String rawPassword) {
        return Password.from(rawPassword).getValue();
    }
}
