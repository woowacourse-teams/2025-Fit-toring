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

    private final ImageService imageService;
    private final CertificateService certificateService;

    private final MentoringRepository mentoringRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMentoringRepository categoryMentoringRepository;
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
            imageService.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
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
        Image image = imageService.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
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

        Image profileImage = saveProfileImage(dto.profileImage(), savedMentoring);

        certificateService.certificateMapping(dto, savedMentoring);
        return MentoringResponse.from(savedMentoring, categoryTitles, profileImage);
    }

    private void categoryMapping(List<String> categoryTitles, Mentoring savedMentoring) {
        for (String categoryTitle : categoryTitles) {
            Category category = categoryRepository.findByTitle(categoryTitle)
                    .orElseThrow(() -> new CategoryNotFoundException(BusinessErrorMessage.CATEGORY_NOT_FOUND.getMessage()));
            CategoryMentoring categoryMentoring = new CategoryMentoring(category, savedMentoring);
            categoryMentoringRepository.save(categoryMentoring);
        }
    }

    private Image saveProfileImage(MultipartFile profileImageFile, Mentoring mentoring) {
        if (profileImageFile == null) {
            return null;
        }
        return imageService.uploadImageToS3(profileImageFile, "profile-image", ImageType.MENTORING_PROFILE, mentoring.getId());
    }
}
