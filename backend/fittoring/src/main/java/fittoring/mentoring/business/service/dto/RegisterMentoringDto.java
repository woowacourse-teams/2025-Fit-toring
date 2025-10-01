package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.MentoringRegisterRequest;
import java.util.List;

public record RegisterMentoringDto(
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

    public static RegisterMentoringDto of(
            Long memberId,
            MentoringRegisterRequest request
    ) {
        return new RegisterMentoringDto(
                memberId,
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
