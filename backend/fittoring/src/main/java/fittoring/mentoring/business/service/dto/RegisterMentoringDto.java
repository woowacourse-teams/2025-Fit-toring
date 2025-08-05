package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.MentoringRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record RegisterMentoringDto(
        String mentorInfo,  // 임시 멘토 회원 정보 TODO : 로그인 정보로 멘토 정보 대체하기
        int price,
        List<String> category,
        String introduction,
        int career,
        String content,
        MultipartFile profileImage,
        List<CertificateInfo> certificateInfos,
        List<MultipartFile> certificateImages
) {
    public static RegisterMentoringDto of(MentoringRequest request, MultipartFile profileImage, List<MultipartFile> certificateImages) {
        return new RegisterMentoringDto(
                "테스트멘토정보",
                request.price(),
                request.category(),
                request.introduction(),
                request.career(),
                request.content(),
                profileImage,
                request.certificateInfos(),
                certificateImages);
    }
}
