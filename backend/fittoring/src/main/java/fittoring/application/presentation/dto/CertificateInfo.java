package fittoring.application.presentation.dto;

import fittoring.domain.model.CertificateType;

public record CertificateInfo(
    CertificateType type,
    String title
) {

}
