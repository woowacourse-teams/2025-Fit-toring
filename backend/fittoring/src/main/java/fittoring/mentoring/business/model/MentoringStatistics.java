package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE mentoring_statistics SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "mentoring_statistics")
@Entity
public class MentoringStatistics {

    @Id
    private Long id;

    @JoinColumn(name = "mentoring_id")
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId()
    private Mentoring mentoring;

    @Column(nullable = false)
    private long reservationCount;

    @Column(nullable = false)
    private long reviewCount;

    @Column(nullable = false)
    private long ratingSum;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static MentoringStatistics defaultOf(Mentoring mentoring, LocalDateTime updatedAt) {
        return new MentoringStatistics(
                mentoring,
                0,
                0,
                0,
                updatedAt
        );
    }

    public MentoringStatistics(
            Mentoring mentoring,
            long reservationCount,
            long reviewCount,
            long ratingSum,
            LocalDateTime updatedAt
    ) {
        this(null, mentoring, reservationCount, reviewCount, ratingSum, updatedAt);
    }
}
