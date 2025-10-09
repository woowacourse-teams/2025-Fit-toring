package fittoring.application.mentoring.presentation.dto.response;

import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;

public record CertificateSpecAndImageResponse(
        Long certificateId,
        String title,
        CertificateType type,
        String imageUrl
) {

    public static CertificateSpecAndImageResponse of(Certificate certificate, String imageUrl) {
        return new CertificateSpecAndImageResponse(
                certificate.getId(),
                certificate.getTitle(),
                certificate.getType(),
                imageUrl
        );
    }
}
