package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.CertificateType;

public record CertificateInfo(
        CertificateType type,
        String title
) {
}
