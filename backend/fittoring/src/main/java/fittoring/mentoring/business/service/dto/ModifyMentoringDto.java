package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.MentoringModifyRequest;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

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
        MultipartFile profileImageFile,
        List<CertificateInfo> certificateInfos,
        List<MultipartFile> certificateImages
) {

    public static ModifyMentoringDto of(
            Long mentoringId,
            Long mentorId,
            MentoringModifyRequest request,
            MultipartFile profileImageFile,
            List<MultipartFile> certificateImages
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
                profileImageFile,
                request.certificateInfos(),
                certificateImages);
    }
}
