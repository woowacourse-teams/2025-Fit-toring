package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentoringService {

    private final MentoringRepository mentoringRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMentoringRepository categoryMentoringRepository;
    private final ImageRepository imageRepository;

    public List<MentoringResponse> getAllMentoring(
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
            List<String> categoryTitles = getCategoryTitlesByMentoring(mentoring);

            imageRepository.findByImageTypeAndRelationId(ImageType.MENTORING, mentoring.getId())
                    .ifPresentOrElse(
                            image -> mentoringResponses.add(MentoringResponse.from(mentoring, categoryTitles, image)),
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

    private void validateExistCategoryTitle(String categoryTitle1) {
        if (categoryTitle1 != null && !categoryRepository.existsByTitle(categoryTitle1)) {
            throw new IllegalArgumentException("존재하지 않는 카테고리가 포함되어 있습니다.");
        }
    }

    private List<String> getCategoryTitlesByMentoring(Mentoring mentoring) {
        List<CategoryMentoring> categoriesByMentoring = categoryMentoringRepository.findAllByMentoringId(
                mentoring.getId());
        return getCategoryTitlesByMentoring(categoriesByMentoring);
    }

    private List<String> getCategoryTitlesByMentoring(List<CategoryMentoring> categoriesByMentoring) {
        List<String> categoryTitles = new ArrayList<>();
        for (CategoryMentoring categoryMentoring : categoriesByMentoring) {
            categoryTitles.add(categoryMentoring.getCategoryTitle());
        }
        return categoryTitles;
    }
}
