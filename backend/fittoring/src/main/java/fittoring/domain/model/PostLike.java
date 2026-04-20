package fittoring.domain.model;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "post_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_like_post_id_actor_key_hash",
                columnNames = {"post_id", "actor_key_hash"}
        )
)
@Entity
public class PostLike {

    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(name = "actor_key_hash", nullable = false, length = 64)
    private String actorKeyHash;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private PostLike(Post post, String actorKeyHash) {
        this.post = post;
        this.actorKeyHash = actorKeyHash;
    }

    public static PostLike of(Post post, String actorKeyHash) {
        return new PostLike(post, actorKeyHash);
    }
}
