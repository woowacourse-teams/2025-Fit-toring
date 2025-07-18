package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.MentoringService;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MentoringController {

    private final MentoringService mentoringService;

    @GetMapping("/mentorings")
    public ResponseEntity<List<MentoringResponse>> getAllMentoring() {
        return ResponseEntity.ok()
                .body(mentoringService.getAllMentoring());
    }
}
