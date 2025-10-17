package fittoring.admin.presentation.dto;

import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Status;
import java.time.LocalDateTime;

public record AdminCertificateResponse(
    Long id,
    String mentorName,
    String certificateName,
    CertificateType certificateType,
    Status certificateStatus,
    LocalDateTime createdAt
) {

    public static fittoring.application.mentoring.presentation.dto.response.CertificateResponse from(Certificate certificate) {
        return new fittoring.application.mentoring.presentation.dto.response.CertificateResponse(
            certificate.getId(),
            certificate.getMentorName(),
            certificate.getTitle(),
            certificate.getType(),
            certificate.getVerificationStatus(),
            certificate.getCreatedAt()
        );
    }
}

