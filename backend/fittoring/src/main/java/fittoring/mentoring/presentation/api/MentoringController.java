package fittoring.mentoring.presentation.api;

import fittoring.mentoring.business.service.MentoringService;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.presentation.dto.MentoringRequest;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import fittoring.mentoring.presentation.dto.MentoringResponse;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @RequestPart("data") MentoringRequest request,
            @RequestPart(value = "image", required = false) MultipartFile profileImage,
            @RequestPart(value = "certificateImages", required = false) List<MultipartFile> certificateImages
    ) {
        RegisterMentoringDto dto = RegisterMentoringDto.of(request, profileImage, certificateImages);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentoringService.registerMentoring(dto));
    }
}
