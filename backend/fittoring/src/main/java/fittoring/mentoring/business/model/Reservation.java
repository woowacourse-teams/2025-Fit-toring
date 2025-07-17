package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String menteeName;

    @Column(nullable = false, unique = true)
    private String menteePhone;

    private String context;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Mentoring mentoring;

    public Reservation(String menteeName, String menteePhone, String context, Mentoring mentoring) {
        this(null, menteeName, menteePhone, context, LocalDateTime.now(), mentoring);
    }

    public String getMenteeName() {
        return menteeName;
    }

    public String getMenteePhone() {
        return menteePhone;
    }

    public String getMentorName() {
        return mentoring.getMentorName();
    }

    public String getMentorPhone() {
        return mentoring.getMentorPhone();
    }
}
