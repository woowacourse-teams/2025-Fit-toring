package fittoring.mentoring.presentation.dto;

import java.util.List;

public record MentoringRequest(
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        List<CertificateInfo> certificateInfos
) {
}
