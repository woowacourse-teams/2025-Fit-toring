package fittoring.mentoring.presentation.api;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.MentoringService;
import fittoring.mentoring.business.service.dto.ModifyMentoringDto;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.presentation.dto.MentoringModifyRequest;
import fittoring.mentoring.presentation.dto.MentoringRegisterRequest;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class MentoringController {

    private final MentoringService mentoringService;

    @AuthRequired
    @PostMapping("/mentorings")
    public ResponseEntity<Void> registerMentoring(
            @Login LoginInfo loginInfo,
            @RequestPart("data") MentoringRegisterRequest request,
            @RequestPart(value = "image", required = false) MultipartFile profileImage,
            @RequestPart(value = "certificateImages", required = false) List<MultipartFile> certificateImages
    ) {
        RegisterMentoringDto dto = RegisterMentoringDto.of(
                loginInfo.memberId(),
                request,
                profileImage,
                certificateImages
        );
        mentoringService.registerMentoring(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/mentorings")
    public ResponseEntity<List<MentoringSummaryResponse>> getMentoringSummaries(
            @RequestParam(required = false) String categoryTitle1,
            @RequestParam(required = false) String categoryTitle2,
            @RequestParam(required = false) String categoryTitle3
    ) {
        List<MentoringSummaryResponse> responseBody = mentoringService.findMentoringSummaries(
                categoryTitle1,
                categoryTitle2,
                categoryTitle3
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @GetMapping("/mentorings/{mentoringId}")
    public ResponseEntity<MentoringResponse> getMentoring(@PathVariable("mentoringId") Long id) {
        MentoringResponse response = mentoringService.getMentoringWithRelationsById(id);
        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @PutMapping("/mentorings/{mentoringId}")
    public ResponseEntity<Void> modifyMentoring(
            @PathVariable("mentoringId") Long mentoringId,
            @Login LoginInfo loginInfo,
            @RequestPart("data") MentoringModifyRequest requestBody,
            @RequestPart(value = "image", required = false) MultipartFile profileImage,
            @RequestPart(value = "certificateImages", required = false) List<MultipartFile> certificateImages
    ) {
        ModifyMentoringDto mentoringModifyDto = ModifyMentoringDto.of(
                mentoringId,
                loginInfo.memberId(),
                requestBody,
                profileImage,
                certificateImages
        );
        mentoringService.modifyMentoring(mentoringModifyDto);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @AuthRequired
    @GetMapping("/mentorings/mine")
    public ResponseEntity<MentoringResponse> getMentoringMine(@Login LoginInfo loginInfo) {
        MentoringResponse response = mentoringService.getMentoringWithRelationsByMentorId(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }
}
