package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminCertificateResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CertificateNotFoundException;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.presentation.dto.response.CertificateDetailResponse;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
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

    @Transactional(readOnly = true)
    public PageResult<AdminCertificateResponse> getAllCertificatesPaged(Long memberId, Status status, int page,
                                                                        int size) {
        List<Long> certificateIds = certificateRepository.findCertificateIdsForAdmin(status, page, size);
        List<AdminCertificateResponse> certificates = certificateRepository.findCertificatesByIdsOrdered(
                certificateIds);
        long total = certificateRepository.countByStatus(status);
        int totalPages = (int) Math.max(1, (total + size - 1) / size);
        return new PageResult<>(certificates, page, certificates.size(), total, totalPages);
    }


    @Transactional(readOnly = true)
    public CertificateDetailResponse getCertificate(Long memberId, Long certificateId) {
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

    @Transactional
    public void approveCertificate(Long memberId, Long certificateId) {
        Certificate certificate = getCertificateOne(certificateId);
        certificate.approve();
    }

    @Transactional
    public void rejectCertificate(Long memberId, Long certificateId) {
        Certificate certificate = getCertificateOne(certificateId);
        certificate.reject();
    }
}
