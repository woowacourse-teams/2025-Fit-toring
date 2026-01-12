package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminMemberService;
import fittoring.config.auth.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin/members")
@RestController
public class AdminMemberController {

    private final AdminMemberService memberService;

    @Admin
    @GetMapping
    public ResponseEntity<PageResult<AdminMemberResponse>> getMembers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResult<AdminMemberResponse> response = memberService.findAllForAdminPaged(page, size);
        return ResponseEntity.ok(response);
    }
}
