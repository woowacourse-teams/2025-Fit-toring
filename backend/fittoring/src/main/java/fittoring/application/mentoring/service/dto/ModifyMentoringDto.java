package fittoring.application.mentoring.service.dto;

import fittoring.application.mentoring.presentation.dto.request.CertificateInfoRequest;
import fittoring.application.mentoring.presentation.dto.request.MentoringModifyRequest;

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
        List<CertificateInfoRequest> certificateInfoRequests,
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
                request.certificateInfoRequests(),
                certificateImages);
    }
}
