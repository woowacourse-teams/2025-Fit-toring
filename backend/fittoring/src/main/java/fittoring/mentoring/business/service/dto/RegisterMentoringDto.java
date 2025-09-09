package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.MentoringRegisterRequest;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record RegisterMentoringDto(
        Long mentorId,
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        String chatUrl,

        MultipartFile profileImage,
        List<CertificateInfo> certificateInfos,
        List<MultipartFile> certificateImages
) {

    public static RegisterMentoringDto of(
            Long memberId,
            MentoringRegisterRequest request,
            MultipartFile profileImageFile,
            List<MultipartFile> certificateImages
    ) {
        return new RegisterMentoringDto(
                memberId,
                request.price(),
                request.category(),
                request.introduction(),
                request.career(),
                request.content(),
                request.chatUrl(),
                profileImageFile,
                request.certificateInfos(),
                certificateImages);
    }
}
