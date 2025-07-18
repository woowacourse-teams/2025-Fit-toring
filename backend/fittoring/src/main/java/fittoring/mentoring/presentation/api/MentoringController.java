package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.service.MentoringService;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MentoringController {

    private final MentoringService mentoringService;

    @GetMapping("/mentorings")
    public ResponseEntity<List<MentoringResponse>> getAllMentoring(
            @RequestParam(required = false) String categoryTitle1,
            @RequestParam(required = false) String categoryTitle2,
            @RequestParam(required = false) String categoryTitle3
    ) {
        return ResponseEntity.ok()
                .body(mentoringService.getAllMentoring(categoryTitle1, categoryTitle2, categoryTitle3));
    }


}
