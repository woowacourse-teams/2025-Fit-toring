package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.CertificateNotFoundException;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.exception.ImageNotFoundException;
import fittoring.mentoring.business.exception.InvalidCertificateException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.CertificateType;
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

    public void mapCertificatesToMentoring(
            List<CertificateInfo> certificateInfos,
            List<MultipartFile> certificateImageFiles,
            Mentoring mentoring
    ) {
        if (validateCertificateRequestData(certificateInfos, certificateImageFiles)) {
            saveAllCertificates(certificateInfos, certificateImageFiles, mentoring);
        }
    }

    private boolean validateCertificateRequestData(List<CertificateInfo> certificateInfos,
                                                   List<MultipartFile> certificateImageFiles) {
        if (certificateInfos == null || certificateImageFiles == null) {
            return false;
        }
        if (certificateInfos.size() != certificateImageFiles.size()) {
            throw new InvalidCertificateException(BusinessErrorMessage.CERTIFICATE_INFO_IMAGE_MISMATCH.getMessage());
        }
        for (CertificateInfo info : certificateInfos) {
            if (info.title().isEmpty() || CertificateType.inValidCertificateType(info.type().name())) {
                throw new InvalidCertificateException(
                        BusinessErrorMessage.INVALID_CERTIFICATE_INFO.getMessage() + info.type().name() + " "
                        + info.title());
            }
        }
        return true;
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

    public void deleteAll(List<Certificate> certificates) {
        certificateRepository.deleteAll(certificates);
    }
}
