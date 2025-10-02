package fittoring.application.business.model;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE mentoring SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "mentoring")
@Entity
public class Mentoring {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private int price;

    private Integer career;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String introduction;

    @Column(name = "chat_url", columnDefinition = "TEXT", nullable = false)
    private String chatUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Getter
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Getter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member mentor;

    public Mentoring(
            Member member,
            int price,
            Integer career,
            String content,
            String introduction,
            String chatUrl
    ) {
        this(null, price, career, content, introduction, chatUrl, null, false, null, member);
    }

    public void modify(
            int price,
            Integer career,
            String content,
            String introduction,
            String chatUrl
    ) {
        this.price = price;
        this.career = career;
        this.content = content;
        this.introduction = introduction;
        this.chatUrl = chatUrl;
    }

    public boolean isCreatedByMember(Long memberId) {
        return this.mentor.isSameIdWith(memberId);
    }

    public String getMentorName() {
        return this.mentor.getName();
    }
}
