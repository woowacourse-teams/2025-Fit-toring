package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.CertificateNotFoundException;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.exception.ImageNotFoundException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.repository.CertificateRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.service.dto.CertificateDeleteDto;
import fittoring.mentoring.presentation.dto.CertificateDetailResponse;
import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.CertificateResponse;
import java.util.ArrayList;
import java.util.List;
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

    public void mapCertificatesToMentoring(
            List<CertificateInfo> certificateInfos,
            Mentoring mentoring
    ) {
        List<CertificateInfo> validCertificateInfos = new ArrayList<>();
        for (CertificateInfo certificateInfo : certificateInfos) {
            if (presignedUrlService.isObjectExists(certificateInfo.imageUrl())) {
                validCertificateInfos.add(certificateInfo);
            }
        }
        saveAllCertificates(validCertificateInfos, mentoring);
    }

    private void saveAllCertificates(
            List<CertificateInfo> certificateInfos,
            Mentoring savedMentoring
    ) {
        List<Image> certificateImages = new ArrayList<>();
        for (int i = 0; i < certificateInfos.size(); i++) {
            CertificateInfo certificateInfo = certificateInfos.get(i);
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
            CertificateInfo request,
            Mentoring mentoring
    ) {
        final Certificate certificate = new Certificate(request.type(), request.title(), mentoring);
        final Certificate savedCertificate = certificateRepository.save(certificate);
        return savedCertificate.getId();
    }

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
