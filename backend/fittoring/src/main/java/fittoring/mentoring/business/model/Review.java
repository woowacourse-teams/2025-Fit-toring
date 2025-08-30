package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
@Entity
public class Review {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(columnDefinition = "TINYINT", nullable = false)
    private int rating;

    @Getter
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Getter
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Getter
    @Column(nullable = false)
    private boolean isDeleted;

    @Getter
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member mentee;

    public Review(int rating, String content, Reservation reservation, Member mentee) {
        this(null, rating, content, null, false, null, reservation, mentee);
    }

    public void modify(Integer rating, String content) {
        if (rating != null) {
            this.rating = rating;
        }
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }

    public Long getMenteeId() {
        return mentee.getId();
    }

    public String getMenteeName() {
        return mentee.getName();
    }
}
