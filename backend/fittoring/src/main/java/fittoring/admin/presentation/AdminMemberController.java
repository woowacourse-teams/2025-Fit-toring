package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminMemberService;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.admin.presentation.dto.AdminMemberResponse;
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

    @AuthRequired
    @GetMapping
    public ResponseEntity<PageResult<AdminMemberResponse>> getMembers(
            @Login LoginInfo loginInfo,
            @RequestParam(defaultValue = "1") int page
    ) {
        PageResult<AdminMemberResponse> response = memberService.findAllForAdminPaged(loginInfo.memberId(), page, 20);
        return ResponseEntity.ok(response);
    }
}
