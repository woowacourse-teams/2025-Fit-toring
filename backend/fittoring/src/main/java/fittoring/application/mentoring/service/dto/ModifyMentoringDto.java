package fittoring.application.mentoring.service.dto;

import fittoring.application.mentoring.presentation.dto.request.CertificateInfoRequest;
import fittoring.application.mentoring.presentation.dto.request.MentoringModifyRequest;
import java.util.List;

public record ModifyMentoringDto(
        Long mentoringId,
        Long mentorId,
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        String profileImageUrl,
        List<CertificateInfoRequest> certificateInfos
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
                request.profileImageUrl(),
                request.certificateInfoRequests()
        );
    }
}
