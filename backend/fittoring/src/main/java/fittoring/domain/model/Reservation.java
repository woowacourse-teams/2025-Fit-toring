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
@SQLDelete(sql = "UPDATE reservation SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "reservation")
@Entity
public class Reservation {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Status status;

    @Getter
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Getter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Mentoring mentoring;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member mentee;

    public Reservation(String content, Status status, Mentoring mentoring, Member mentee) {
        this(null, content, null, status, false, null, mentoring, mentee);
    }

    public boolean isComplete() {
        return this.status.isComplete();
    }

    public boolean isCreatedByMember(Long memberId) {
        return this.mentee.isSameIdWith(memberId);
    }

    public void changeStatus(Status updateStatus) {
        this.status.validateReservation(updateStatus);
        this.status = updateStatus;
    }

    public void changeStatusWithoutValidation(Status updateStatus) {
        this.status = updateStatus;
    }

    public boolean isPending() {
        return this.status.isPending();
    }

    public String getChatUrlOfMentoring() {
        return mentoring.getChatUrl();
    }

    public String getMenteeName() {
        return mentee.getName();
    }

    public String getMentorName() {
        return mentoring.getMentorName();
    }

    public String getMenteePhone() {
        return mentee.getPhoneNumber();
    }

    public String getMentorPhone() {
        return mentoring.getMentor().getPhoneNumber();
    }

    public String getStatus() {
        return status.name();
    }
}
