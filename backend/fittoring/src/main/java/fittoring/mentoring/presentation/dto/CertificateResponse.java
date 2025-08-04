package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.CertificateType;
import fittoring.mentoring.business.model.Status;
import java.time.LocalDateTime;

public record CertificateResponse(
        Long id,
        String mentorName,
        String certificateName,
        CertificateType certificateType,
        Status certificateStatus,
        LocalDateTime createdAt
) {

    public static CertificateResponse from(Certificate certificate) {
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
