package fittoring.application.mentoring.presentation.dto.request;

import fittoring.domain.model.CertificateType;

public record CertificateInfoRequest(
        CertificateType type,
        String title,
        String imageUrl
) {

}
