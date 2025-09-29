package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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

    @Getter
    @Column(nullable = false)
    private long reviewCount;

    @Getter
    @Column(nullable = false)
    private long ratingSum;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static MentoringStatistics defaultOf(Mentoring mentoring) {
        return new MentoringStatistics(
            null,
            mentoring,
            0,
            0,
            0,
            null
        );
    }
}
