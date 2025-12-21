package fittoring.application.mentoring.presentation;

import fittoring.application.mentoring.presentation.dto.request.MentoringModifyRequest;
import fittoring.application.mentoring.presentation.dto.request.MentoringRegisterRequest;
import fittoring.application.mentoring.presentation.dto.response.MentoringResponse;
import fittoring.application.mentoring.service.MentoringService;
import fittoring.application.mentoring.service.dto.MentoringSummaryPaginationResponse;
import fittoring.application.mentoring.service.dto.ModifyMentoringDto;
import fittoring.application.mentoring.service.dto.RegisterMentoringDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.SortKey;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MentoringController {

    private final MentoringService mentoringService;

    @AuthRequired
    @PostMapping("/mentorings")
    public ResponseEntity<Void> registerMentoring(
            @Login LoginInfo loginInfo,
            @RequestBody MentoringRegisterRequest requestBody
    ) {
        RegisterMentoringDto dto = RegisterMentoringDto.of(
                loginInfo.memberId(),
                requestBody
        );
        mentoringService.registerMentoring(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/mentorings-page")
    public ResponseEntity<MentoringSummaryPaginationResponse> getMentoringSummaryPages(
            @RequestParam(defaultValue = "CREATED_AT") SortKey sortKey,
            @RequestParam(required = false) String cursorCode,
            @RequestParam(required = false) List<Long> categoryIds
    ) {
        if (categoryIds == null) {
            categoryIds = List.of();
        }
        MentoringSummaryPaginationResponse responseBody = mentoringService.findMentoringSummaryPages(
                sortKey,
                cursorCode,
                categoryIds
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
            @RequestBody MentoringModifyRequest requestBody
    ) {
        ModifyMentoringDto mentoringModifyDto = ModifyMentoringDto.of(
                mentoringId,
                loginInfo.memberId(),
                requestBody
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
