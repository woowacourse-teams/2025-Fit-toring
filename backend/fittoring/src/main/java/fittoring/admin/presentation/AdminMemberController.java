package fittoring.admin.presentation;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.application.member.service.MemberService;
import fittoring.admin.presentation.dto.AdminMemberResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin/members")
@RestController
public class AdminMemberController {

    private final MemberService memberService;

    @AuthRequired
    @GetMapping
    public ResponseEntity<List<AdminMemberResponse>> getMembers(@Login LoginInfo loginInfo) {
        List<AdminMemberResponse> response = memberService.findAllForAdmin(loginInfo.memberId());
        return ResponseEntity.ok(response);
    }
}
