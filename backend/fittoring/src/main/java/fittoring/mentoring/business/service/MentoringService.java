package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
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
    private final CategoryMentoringRepository categoryMentoringRepository;
    private final ImageRepository imageRepository;

    public List<MentoringResponse> getAllMentoring() {
        List<Mentoring> mentorings = mentoringRepository.findAll();
        return getMentoringResponses(mentorings);
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
