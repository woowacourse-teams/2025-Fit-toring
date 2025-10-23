package fittoring.application.mentoring.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CertificateNotFoundException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.mentoring.presentation.dto.request.CertificateInfoRequest;
import fittoring.application.mentoring.repository.CertificateRepository;
import fittoring.application.mentoring.service.dto.CertificateDeleteDto;
import fittoring.domain.model.Certificate;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Status;
import fittoring.infrastructure.image.KeyBuilder;
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

    private final CertificateRepository certificateRepository;
    private final ImageService imageService;
    private final PresignedUrlService presignedUrlService;
    private final JsonLogger jsonLogger;
    private final KeyBuilder keyBuilder;

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
        if (certificateInfos == null) {
            return;
        }
        List<Image> certificateImages = new ArrayList<>();
        for (CertificateInfoRequest certificateInfo : certificateInfos) {
            Long certificateId = saveCertificate(certificateInfo, savedMentoring);
            String baseName = keyBuilder.extractBaseNameFromUrl(certificateInfo.imageUrl());
            certificateImages.add(new Image(
                    certificateInfo.imageUrl(),
                    ImageType.CERTIFICATE,
                    certificateId,
                    baseName
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
