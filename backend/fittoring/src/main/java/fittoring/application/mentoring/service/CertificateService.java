package fittoring.application.mentoring.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CertificateNotFoundException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.presentation.dto.request.CertificateInfoRequest;
import fittoring.application.mentoring.presentation.dto.response.CertificateDetailResponse;
import fittoring.application.mentoring.presentation.dto.response.CertificateResponse;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.application.mentoring.service.dto.CertificateDeleteDto;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Status;
import fittoring.logging.JsonLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CertificateService {

    private final MemberRepository memberRepository;
    private final CertificateRepository certificateRepository;
    private final ImageService imageService;
    private final PresignedUrlService presignedUrlService;
    private final JsonLogger jsonLogger;

    public void mapCertificatesToMentoring(
            List<CertificateInfoRequest> certificateInfoRequests,
            Mentoring mentoring
    ) {
        List<CertificateInfoRequest> validCertificateInfos = filterValidCertificates(
                certificateInfoRequests,
                mentoring
        );
        saveAllCertificates(validCertificateInfos, mentoring);
    }

    private List<CertificateInfoRequest> filterValidCertificates(
            List<CertificateInfoRequest> certificateInfoRequests,
            Mentoring mentoring
    ) {
        List<CertificateInfoRequest> validCertificateInfos = new ArrayList<>();
        for (CertificateInfoRequest certificateInfo : certificateInfoRequests) {
            if (presignedUrlService.isObjectExistsFromKey(certificateInfo.imageUrl())) {
                validCertificateInfos.add(certificateInfo);
            } else {
                jsonLogger.warn(
                        "자격증 이미지 검증 실패 (S3 객체 없음)",
                        Map.of(
                                "imageUrl", certificateInfo.imageUrl(),
                                "mentoringId", mentoring.getId(),
                                "certificateTitle", certificateInfo.title()
                        )
                );
            }
        }
        return validCertificateInfos;
    }

    private void saveAllCertificates(
            List<CertificateInfoRequest> certificateInfos,
            Mentoring savedMentoring
    ) {
        List<Image> certificateImages = new ArrayList<>();
        for (CertificateInfoRequest certificateInfo : certificateInfos) {
            Long certificateId = saveCertificate(certificateInfo, savedMentoring);
            certificateImages.add(new Image(
                    certificateInfo.imageUrl(),
                    ImageType.CERTIFICATE,
                    certificateId
            ));
        }
        imageService.saveAll(certificateImages);
    }

    private Long saveCertificate(
            CertificateInfoRequest request,
            Mentoring mentoring
    ) {
        final Certificate certificate = new Certificate(request.type(), request.title(), mentoring);
        final Certificate savedCertificate = certificateRepository.save(certificate);
        return savedCertificate.getId();
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> getAllCertificates(Long memberId, Status status) {
        checkAdminAuthority(memberId);
        List<Certificate> certificates = findCertificates(status);
        return certificates.stream()
                .map(CertificateResponse::from)
                .toList();
    }

    private List<Certificate> findCertificates(Status status) {
        if (status == null) {
            return certificateRepository.findAll();
        }
        return certificateRepository.findByVerificationStatus(status);
    }

    private void checkAdminAuthority(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        if (MemberRole.isNotAdmin(member.getRole())) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public CertificateDetailResponse getCertificate(Long memberId, Long certificateId) {
        checkAdminAuthority(memberId);
        Certificate certificate = getCertificateOne(certificateId);
        Image certificateImage = imageService.findByImageTypeAndRelationId(ImageType.CERTIFICATE, certificateId)
                .orElseThrow(() -> new ImageNotFoundException(
                        BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage()
                ));
        return CertificateDetailResponse.of(certificate, certificateImage);
    }

    private Certificate getCertificateOne(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException(
                        BusinessErrorMessage.CERTIFICATE_NOT_FOUND.getMessage()
                ));
    }

    public List<Certificate> findAllByMentoringId(Long mentoringId) {
        return certificateRepository.findAllByMentoringId(mentoringId);
    }

    @Transactional
    public void approveCertificate(Long memberId, Long certificateId) {
        checkAdminAuthority(memberId);
        Certificate certificate = getCertificateOne(certificateId);
        certificate.approve();
    }

    @Transactional
    public void rejectCertificate(Long memberId, Long certificateId) {
        checkAdminAuthority(memberId);
        Certificate certificate = getCertificateOne(certificateId);
        certificate.reject();
    }

    @Transactional
    public void deleteCertificate(CertificateDeleteDto dto) {
        Certificate certificate = certificateRepository.findById(dto.certificateId())
                .orElseThrow(() -> new CertificateNotFoundException(
                        BusinessErrorMessage.CERTIFICATE_NOT_FOUND.getMessage()));
        validateCertificateOwner(certificate, dto.mentorId());
        certificateRepository.delete(certificate);
    }

    private void validateCertificateOwner(Certificate certificate, Long mentorId) {
        if (certificate.getMentorId().equals(mentorId)) {
            return;
        }
        throw new ForbiddenException(BusinessErrorMessage.NOT_CERTIFICATE_OWNER.getMessage());
    }
}
