package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.CertificateNotFoundException;
import fittoring.mentoring.business.exception.ImageNotFoundException;
import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.repository.CertificateRepository;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.presentation.dto.CertificateDetailResponse;
import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.CertificateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class CertificateService {

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

    public Page<CertificateResponse> getAllCertificates(Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Certificate> certificates;
        if (status == null) {
            certificates = certificateRepository.findAll(pageable);
        } else {
            certificates = certificateRepository.findByVerificationStatus(status, pageable);
        }
        return certificates.map(CertificateResponse::from);
    }

    public CertificateDetailResponse getCertificate(Long certificateId) {
        Certificate certificate = getCertificateOne(certificateId);
        Image certificateImage = imageService.findByImageTypeAndRelationId(ImageType.CERTIFICATE, certificateId)
                .orElseThrow(() -> new ImageNotFoundException(
                        BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage()
                ));
        return CertificateDetailResponse.of(certificate, certificateImage);
    }

    @Transactional
    public void approveCertificate(Long certificateId) {
        Certificate certificate = getCertificateOne(certificateId);
        certificate.approve();
    }

    @Transactional
    public void rejectCertificate(Long certificateId) {
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
