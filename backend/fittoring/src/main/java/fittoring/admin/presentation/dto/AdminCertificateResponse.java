package fittoring.admin.presentation.dto;

import fittoring.application.mentoring.presentation.dto.response.CertificateResponse;
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

    public static CertificateResponse from(
            Certificate certificate) {
        return new CertificateResponse(
                certificate.getId(),
                certificate.getMentorName(),
                certificate.getTitle(),
                certificate.getType(),
                certificate.getVerificationStatus(),
                certificate.getCreatedAt()
        );
    }
}

