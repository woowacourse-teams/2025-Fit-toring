package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.CategoryNotFoundException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.*;
import fittoring.mentoring.business.repository.*;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.infra.S3Uploader;
import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.S3UploadException;
import fittoring.mentoring.presentation.dto.CertificateInfo;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import fittoring.mentoring.presentation.dto.MentoringResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MentoringService {

    private final MentoringRepository mentoringRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMentoringRepository categoryMentoringRepository;
    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;
    private final CertificateRepository certificateRepository;


    public List<MentoringSummaryResponse> findMentoringSummaries(
            String categoryTitle1,
            String categoryTitle2,
            String categoryTitle3
    ) {
        List<MentoringSummaryResponse> mentoringSummaryResponse;
        List<MentoringResponse> mentoringResponses = findMentorings(categoryTitle1, categoryTitle2, categoryTitle3);

        mentoringSummaryResponse = mentoringResponses.stream()
                .map(MentoringSummaryResponse::from)
                .collect(Collectors.toList());

        return mentoringSummaryResponse;
    }

    private List<MentoringResponse> findMentorings(
            String categoryTitle1,
            String categoryTitle2,
            String categoryTitle3
    ) {
        if (isNoCategoryFilter(categoryTitle1, categoryTitle2, categoryTitle3)) {
            List<Mentoring> allMentoring = mentoringRepository.findAll();
            return getMentoringResponses(allMentoring);
        }
        validateAllCategoryTitle(categoryTitle1, categoryTitle2, categoryTitle3);

        List<Mentoring> filteredMentorings = mentoringRepository.findAllMentoringWithFilter(
                categoryTitle1,
                categoryTitle2,
                categoryTitle3
        );

        return getMentoringResponses(filteredMentorings);
    }

    private boolean isNoCategoryFilter(String categoryTitle1, String categoryTitle2, String categoryTitle3) {
        return categoryTitle1 == null
                && categoryTitle2 == null
                && categoryTitle3 == null;
    }

    private List<MentoringResponse> getMentoringResponses(List<Mentoring> mentorings) {
        List<MentoringResponse> mentoringResponses = new ArrayList<>();
        for (Mentoring mentoring : mentorings) {
            List<String> categoryTitles = getCategoryTitlesByMentoringId(mentoring.getId());

            imageRepository.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
                    .ifPresentOrElse(
                            image -> mentoringResponses.add(
                                    MentoringResponse.from(mentoring, categoryTitles, image)),
                            () -> mentoringResponses.add(MentoringResponse.from(mentoring, categoryTitles))
                    );
        }
        return mentoringResponses;
    }

    private void validateAllCategoryTitle(String categoryTitle1, String categoryTitle2, String categoryTitle3) {
        validateExistCategoryTitle(categoryTitle1);
        validateExistCategoryTitle(categoryTitle2);
        validateExistCategoryTitle(categoryTitle3);
    }

    private void validateExistCategoryTitle(String categoryTitle) {
        if (categoryTitle != null && !categoryRepository.existsByTitle(categoryTitle)) {
            throw new CategoryNotFoundException(BusinessErrorMessage.CATEGORY_NOT_FOUND.getMessage());
        }
    }

    private List<String> getCategoryTitlesByMentoringId(Long mentoringId) {
        List<CategoryMentoring> categoryMappingsByMentoring = categoryMentoringRepository.findAllByMentoringId(
                mentoringId);
        return getCategoryTitlesByMentoring(categoryMappingsByMentoring);
    }

    private List<String> getCategoryTitlesByMentoring(List<CategoryMentoring> categoryMappingsByMentoring) {
        return categoryMappingsByMentoring.stream()
                .map(CategoryMentoring::getCategoryTitle)
                .collect(Collectors.toList());
    }

    public MentoringResponse getMentoring(final Long id) {
        Mentoring mentoring = mentoringRepository.findById(id)
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));

        List<String> categoryTitles = getCategoryTitlesByMentoringId(mentoring.getId());
        Image image = imageRepository.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
                .orElse(null);

        if (image == null) {
            return MentoringResponse.from(mentoring, categoryTitles);
        }
        return MentoringResponse.from(mentoring, categoryTitles, image);
    }

    public MentoringResponse registerMentoring(RegisterMentoringDto dto) {
        final Mentoring mentoring = new Mentoring(dto.mentorInfo(), dto.mentorInfo(), dto.price(), dto.career(), dto.content(), dto.introduction());
        final Mentoring savedMentoring = mentoringRepository.save(mentoring);

        List<String> categoryTitles = dto.category();
        categoryMapping(categoryTitles, savedMentoring);

        Image profileImage = saveProfileImage(dto, savedMentoring);

        certificateMapping(dto, savedMentoring);

        return MentoringResponse.from(savedMentoring, categoryTitles, profileImage);
    }

    private void categoryMapping(List<String> categoryTitles, Mentoring savedMentoring) {
        for (String categoryTitle : categoryTitles) {
            Category category = categoryRepository.findByTitle(categoryTitle);
            CategoryMentoring categoryMentoring = new CategoryMentoring(category, savedMentoring);
            categoryMentoringRepository.save(categoryMentoring);
        }
    }

    private Image saveProfileImage(RegisterMentoringDto dto, Mentoring mentoring) {
        if (dto.profileImage() == null) {
            return null;
        }
        try {
            String profileUrl = s3Uploader.upload(dto.profileImage(), "profile-image");
            Image profile = new Image(profileUrl, ImageType.MENTORING_PROFILE, mentoring.getId());
            return imageRepository.save(profile);
        } catch (IOException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage());
        }
    }

    private void certificateMapping(RegisterMentoringDto dto, Mentoring savedMentoring) {
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
        try {
            String certificateUrl = s3Uploader.upload(certificateImageFile, "certificate-image");
            Image certificateImage = new Image(certificateUrl, ImageType.CERTIFICATE, savedCertificate.getId());
            imageRepository.save(certificateImage);
        } catch (IOException e) {
            throw new S3UploadException(InfraErrorMessage.S3_UPLOAD_ERROR.getMessage());
        }
    }

}
