package fittoring.application.mentoring.service.dto;

import fittoring.application.mentoring.presentation.dto.request.MentoringModifyRequest;
import fittoring.mentoring.presentation.dto.CertificateInfo;
import java.util.List;

public record ModifyMentoringDto(
        Long mentoringId,
        Long mentorId,
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        String chatUrl,
        String profileImageUrl,
        List<CertificateInfo> certificateInfos
) {

    public static ModifyMentoringDto of(
            Long mentoringId,
            Long mentorId,
            MentoringModifyRequest request
    ) {
        return new ModifyMentoringDto(
                mentoringId,
                mentorId,
                request.price(),
                request.category(),
                request.introduction(),
                request.career(),
                request.content(),
                request.chatUrl(),
                request.profileImageUrl(),
                request.certificateInfos()
        );
    }
}
