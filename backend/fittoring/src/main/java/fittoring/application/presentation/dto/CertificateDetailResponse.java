package fittoring.application.presentation.dto;

import fittoring.application.business.model.Certificate;
import fittoring.application.business.model.CertificateType;
import fittoring.application.business.model.Image;
import fittoring.application.business.model.Status;
import java.time.LocalDateTime;

public record CertificateDetailResponse(
        Long id,
        String mentorName,
        String certificateName,
        CertificateType certificateType,
        Status certificateStatus,
        LocalDateTime createdAt,
        String imageUrl
) {

    public static CertificateDetailResponse of(Certificate certificate, Image certificateImage) {
        return new CertificateDetailResponse(
                certificate.getId(),
                certificate.getMentorName(),
                certificate.getTitle(),
                certificate.getType(),
                certificate.getVerificationStatus(),
                certificate.getCreatedAt(),
                certificateImage.getUrl()
        );
    }
}
