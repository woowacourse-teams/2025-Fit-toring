package fittoring.application.mentoring.service.dto;

import fittoring.application.mentoring.presentation.dto.request.CertificateInfoRequest;
import fittoring.application.mentoring.presentation.dto.request.MentoringRegisterRequest;
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

        List<CertificateInfoRequest> certificateInfos
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
                request.certificateInfoRequests()
        );
    }
}
