package fittoring.admin.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CertificateNotFoundException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.presentation.dto.response.CertificateDetailResponse;
import fittoring.application.mentoring.presentation.dto.response.CertificateResponse;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminCertificateService {

    private final ImageService imageService;
    private final CertificateRepository certificateRepository;
    private final MemberRepository memberRepository;

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
}
