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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
@Entity
public class Reservation {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String context;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Mentoring mentoring;

    @Getter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Member mentee;

    public Reservation(String context, Mentoring mentoring, Member mentee) {
        this(null, context, null, mentoring, mentee);
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

    public String getMentorPhone() {
        return mentoring.getMentorPhone();
    }
}
