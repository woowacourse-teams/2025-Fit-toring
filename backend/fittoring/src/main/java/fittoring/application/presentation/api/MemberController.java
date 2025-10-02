package fittoring.application.presentation.api;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.application.service.MemberService;
import fittoring.admin.presentation.dto.AdminActiveStatusResponse;
import fittoring.application.presentation.dto.MyInfoResponse;
import fittoring.application.presentation.dto.MyInfoSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @AuthRequired
    @GetMapping("/members/me")
    public ResponseEntity<MyInfoResponse> getMyInfo(@Login LoginInfo loginInfo) {
        MyInfoResponse memberInfo = memberService.getMemberInfo(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
            .body(memberInfo);
    }

    @AuthRequired
    @GetMapping("/members/summary")
    public ResponseEntity<MyInfoSummaryResponse> getMyInfoSummary(@Login LoginInfo loginInfo) {
        MyInfoSummaryResponse memberInfoSummary = memberService.getMemberInfoSummary(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
            .body(memberInfoSummary);
    }

    @AuthRequired
    @GetMapping("/members/status")
    public ResponseEntity<AdminActiveStatusResponse> getAdminMemberStatus(@Login LoginInfo loginInfo) {
        boolean isActive = memberService.getAdminMemberActiveStatus(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
            .body(new AdminActiveStatusResponse(isActive));
    }
}
