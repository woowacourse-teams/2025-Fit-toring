package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.CertificateNotFoundException;
import fittoring.mentoring.business.exception.ForbiddenMemberException;
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
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.presentation.dto.CertificateDetailResponse;
import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.CertificateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class CertificateService {

    private final MemberRepository memberRepository;
    private final CertificateRepository certificateRepository;
    private final ImageService imageService;

    public void certificateMapping(RegisterMentoringDto dto, Mentoring savedMentoring) {
        List<CertificateInfo> certificateInfos = dto.certificateInfos();
        List<MultipartFile> certificateImageFiles = dto.certificateImages();

        if (validateCertificateRequestData(certificateInfos, certificateImageFiles)) {
            saveAllCertificates(certificateInfos, certificateImageFiles, savedMentoring);
        }
    }

    private boolean validateCertificateRequestData(List<CertificateInfo> certificateInfos,
                                                   List<MultipartFile> certificateImageFiles) {
        return (certificateInfos != null && certificateImageFiles != null)
                && (certificateInfos.size() == certificateImageFiles.size());
    }

    private void saveAllCertificates(List<CertificateInfo> certificateInfos, List<MultipartFile> certificateImageFiles,
                                     Mentoring savedMentoring) {
        for (int i = 0; i < certificateInfos.size(); i++) {
            CertificateInfo certificateInfo = certificateInfos.get(i);
            MultipartFile certificateImageFile = certificateImageFiles.get(i);

            saveCertificate(certificateInfo, certificateImageFile, savedMentoring);
        }
    }

    private void saveCertificate(CertificateInfo request, MultipartFile certificateImageFile, Mentoring mentoring) {
        final Certificate certificate = new Certificate(request.type(), request.title(), mentoring);
        final Certificate savedCertificate = certificateRepository.save(certificate);
        Long certificateId = savedCertificate.getId();

        imageService.uploadImageToS3(certificateImageFile, "certificate-image", ImageType.CERTIFICATE, certificateId);
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
            throw new ForbiddenMemberException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
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

    private Certificate getCertificateOne(Long certificateId) {
        return certificateRepository.findById(certificateId).orElseThrow(
                () -> new CertificateNotFoundException(
                        BusinessErrorMessage.CERTIFICATE_NOT_FOUND.getMessage()
                ));
    }
}
