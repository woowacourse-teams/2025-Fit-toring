package fittoring.mentoring.presentation.api.admin;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.MemberService;
import fittoring.mentoring.presentation.dto.AdminMemberResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/members")
public class AdminMemberController {

    private MemberService memberService;

    @AuthRequired
    @GetMapping
    public ResponseEntity<List<AdminMemberResponse>> getMembers(@Login LoginInfo loginInfo) {
        List<AdminMemberResponse> response = memberService.findAllForAdmin(loginInfo.memberId());
        return ResponseEntity.ok(response);
    }
}
