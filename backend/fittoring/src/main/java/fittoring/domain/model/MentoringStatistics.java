package fittoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE mentoring_statistics SET is_deleted = true, deleted_at = now() WHERE mentoring_id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "mentoring_statistics")
@Entity
public class MentoringStatistics {

    @Getter
    @Column(name = "mentoring_id")
    @Id
    private Long mentoringId;

    @Getter
    @Column(nullable = false)
    private long reservationCount;

    @Getter
    @Column(nullable = false)
    private long reviewCount;

    @Getter
    @Column(nullable = false)
    private long ratingSum;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Getter
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Getter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static MentoringStatistics defaultOf(Mentoring mentoring) {
        return new MentoringStatistics(
            mentoring.getId(),
            0,
            0,
            0,
            null,
            false,
            null
        );
    }

    public double calculateAverageRating() {
        if (reviewCount == 0) {
            return 0.0;
        }
        return (double) ratingSum / reviewCount;
    }
}
