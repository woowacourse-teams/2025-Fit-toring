package fittoring.mentoring.business.model;

import fittoring.mentoring.business.exception.AlreadyProcessedCertificateException;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@SQLDelete(sql = "UPDATE certificate SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "certificate")
@Entity
public class Certificate {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
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

    @Getter
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Getter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Mentoring mentoring;

    public Certificate(CertificateType type, String title, Mentoring mentoring) {
        this(null, type, title, Status.PENDING, null, false, null, mentoring);
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

    public Long getMentorId() {
        return this.mentoring.getMentor().getId();
    }

    public String getMentorName() {
        return this.mentoring.getMentorName();
    }
}
