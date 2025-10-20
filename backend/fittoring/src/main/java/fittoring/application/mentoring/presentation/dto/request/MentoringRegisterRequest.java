package fittoring.application.mentoring.presentation.dto.request;

import java.util.List;

public record MentoringRegisterRequest(
        int price,
        List<String> category,
        String introduction,
        String profileImageUrl,
        int career,
        String content,
        List<CertificateInfoRequest> certificateInfoRequests
) {

}
