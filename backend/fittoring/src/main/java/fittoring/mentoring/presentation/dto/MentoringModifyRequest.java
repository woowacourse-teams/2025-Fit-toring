package fittoring.mentoring.presentation.dto;

import java.util.List;

public record MentoringModifyRequest(
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        String profileImageUrl,
        List<CertificateInfo> certificateInfos
) {

}
