package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.repository.CertificateRepository;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.presentation.dto.CertificateInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    private boolean validateCertificateRequestData(List<CertificateInfo> certificateInfos, List<MultipartFile> certificateImageFiles) {
        return (certificateInfos != null && certificateImageFiles != null)
                && (certificateInfos.size() == certificateImageFiles.size());
    }

    private void saveAllCertificates(List<CertificateInfo> certificateInfos, List<MultipartFile> certificateImageFiles, Mentoring savedMentoring) {
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
}
