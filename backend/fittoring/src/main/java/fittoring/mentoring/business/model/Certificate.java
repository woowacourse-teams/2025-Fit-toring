package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certificate")
@Entity
public class Certificate {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateType type;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Mentoring mentoring;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status verificationStatus;

    public Certificate(CertificateType type, String title, Mentoring mentoring){
        this(null, type, title, mentoring, Status.PENDING);
    }
}
