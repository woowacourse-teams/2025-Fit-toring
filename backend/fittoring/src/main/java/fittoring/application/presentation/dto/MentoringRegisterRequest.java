package fittoring.application.presentation.dto;

import java.util.List;

public record MentoringRegisterRequest(
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        String chatUrl,

        List<CertificateInfo> certificateInfos
) {

}
