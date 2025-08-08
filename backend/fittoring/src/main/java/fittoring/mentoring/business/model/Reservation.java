package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservation")
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Mentoring mentoring;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member mentee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Reservation(String content, Mentoring mentoring, Member mentee, Status status) {
        this(null, content, null, mentoring, mentee, status);
    }

    public void changeStatus(Status updateStatus) {
        this.status.validate(updateStatus);
        this.status = updateStatus;
    }

    public boolean isPending() {
        return this.status.isPending();
    }

    public String getMenteeName() {
        return mentee.getName();
    }

    public String getMenteePhone() {
        return mentee.getPhoneNumber();
    }

    public String getMentorName() {
        return mentoring.getMentorName();
    }

    public String getStatus() {
        return status.getValue();
    }
}
