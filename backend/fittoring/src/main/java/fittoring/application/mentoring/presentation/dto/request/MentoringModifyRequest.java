package fittoring.application.mentoring.presentation.dto.request;

import java.util.List;

public record MentoringModifyRequest(
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        String profileImageUrl,
        List<CertificateInfoRequest> certificateInfoRequests
) {

}
