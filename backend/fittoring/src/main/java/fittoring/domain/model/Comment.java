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
@SQLDelete(sql = "UPDATE comment SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "comment")
@Entity
public class Comment {

    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "guest_password")
    private String guestPassword;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @Column(name = "root_id")
    private Long rootId;

    @Column(name = "parent_id")
    private Long parentId;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static Comment forMember(
            Post post,
            Member member,
            String content,
            boolean isAnonymous,
            String nickname,
            Long rootId,
            Long parentId
    ) {
        return new Comment(
                null,
                content,
                post,
                member,
                nickname,
                null,
                isAnonymous,
                rootId,
                parentId,
                null,
                false,
                null
        );
    }

    public static Comment forGuest(
            Post post,
            String content,
            String nickname,
            String rawPassword,
            Long rootId,
            Long parentId
    ) {
        return new Comment(
                null,
                content,
                post,
                null,
                nickname,
                encryptPassword(rawPassword),
                false,
                rootId,
                parentId,
                null,
                false,
                null
        );
    }

    public void modify(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }

    public boolean isOwnedBy(Long memberId) {
        return member != null && member.isSameIdWith(memberId);
    }

    public boolean isGuestComment() {
        return member == null;
    }

    public void matchGuestPassword(String rawPassword) {
        String encryptedPassword = encryptPassword(rawPassword);
        if (!encryptedPassword.equals(this.guestPassword)) {
            throw new MisMatchPasswordException(BusinessErrorMessage.GUEST_PASSWORD_MISMATCH.getMessage());
        }
    }

    public boolean belongsTo(Long postId) {
        return post.getId().equals(postId);
    }

    private static String encryptPassword(String rawPassword) {
        return Password.from(rawPassword).getValue();
    }
}
