package fittoring.application.presentation.dto;

import fittoring.application.business.model.CertificateType;

public record CertificateInfo(
    CertificateType type,
    String title
) {

}
