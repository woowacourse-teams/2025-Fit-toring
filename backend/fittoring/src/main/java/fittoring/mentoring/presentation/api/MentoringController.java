package fittoring.mentoring.presentation.api;

import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.MentoringService;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.presentation.dto.MentoringRequest;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class MentoringController {

    private final MentoringService mentoringService;

    @GetMapping("/mentorings")
    public ResponseEntity<List<MentoringSummaryResponse>> getMentoringSummaries(
            @RequestParam(required = false) String categoryTitle1,
            @RequestParam(required = false) String categoryTitle2,
            @RequestParam(required = false) String categoryTitle3
    ) {
        return ResponseEntity.ok()
                .body(mentoringService.findMentoringSummaries(categoryTitle1, categoryTitle2, categoryTitle3));
    }

    @GetMapping("/mentorings/{mentoringId}")
    public ResponseEntity<MentoringResponse> getMentoring(@PathVariable("mentoringId") Long id) {
        MentoringResponse response = mentoringService.getMentoring(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mentorings")
    public ResponseEntity<MentoringResponse> registerMentoring(
            @Login LoginInfo loginInfo,
            @RequestPart("data") MentoringRequest request,
            @RequestPart(value = "image", required = false) MultipartFile profileImage,
            @RequestPart(value = "certificateImages", required = false) List<MultipartFile> certificateImages
    ) {
        RegisterMentoringDto dto = RegisterMentoringDto.of(
                loginInfo.memberId(),
                request,
                profileImage,
                certificateImages
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentoringService.registerMentoring(dto));
    }
}
