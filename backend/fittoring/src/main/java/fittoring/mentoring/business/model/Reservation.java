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

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservation")
@Entity
public class Reservation {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String content;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Status status;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Mentoring mentoring;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member mentee;

    public Reservation(String content, Status status, Mentoring mentoring, Member mentee) {
        this(null, content, null, status, mentoring, mentee);
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
