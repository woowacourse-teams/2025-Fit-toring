package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mentoring")
@Entity
public class Mentoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mentorName;

    @Column(nullable = false, unique = true)
    private String mentorPhone;

    @Column(nullable = false)
    private int price;

    private Integer career;

    private String content;

    @Column(nullable = false)
    private String introduction;

    public String getMentorName() {
        return mentorName;
    }

    public String getMentorPhone() {
        return mentorPhone;
    }
}
