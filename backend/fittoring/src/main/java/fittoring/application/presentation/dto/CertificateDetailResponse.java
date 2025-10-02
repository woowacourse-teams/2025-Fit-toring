package fittoring.application.presentation.dto;

import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.Image;
import fittoring.domain.model.Status;
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
