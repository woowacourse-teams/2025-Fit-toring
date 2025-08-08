package fittoring.mentoring.business.model;

import fittoring.mentoring.business.exception.AlreadyProcessedCertificateException;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
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
@Table(name = "certificate")
@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateType type;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status verificationStatus;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Mentoring mentoring;

    public Certificate(CertificateType type, String title, Mentoring mentoring) {
        this(null, type, title, Status.PENDING, null, mentoring);
    }

    public void approve() {
        validateAlreadyProcessedCertificate();
        this.verificationStatus = Status.APPROVED;
    }

    public void reject() {
        validateAlreadyProcessedCertificate();
        this.verificationStatus = Status.REJECTED;
    }

    private void validateAlreadyProcessedCertificate() {
        if (verificationStatus != Status.PENDING) {
            throw new AlreadyProcessedCertificateException(
                    BusinessErrorMessage.ALREADY_PROCESSED_CERTIFICATE.getMessage());
        }
    }

    public String getMentorName() {
        return this.mentoring.getMentorName();
    }
}
