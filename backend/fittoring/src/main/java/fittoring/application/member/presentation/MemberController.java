package fittoring.application.member.presentation;

import fittoring.admin.presentation.dto.AdminActiveStatusResponse;
import fittoring.application.member.presentation.dto.request.MemberInfoUpdateRequest;
import fittoring.application.member.presentation.dto.response.MyInfoResponse;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.member.service.MemberService;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @AuthRequired
    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> getMyInfo(@Login LoginInfo loginInfo) {
        MyInfoResponse memberInfo = memberService.getMemberInfo(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberInfo);
    }

    @AuthRequired
    @PatchMapping("/me")
    public ResponseEntity<Void> updateInfo(@Login LoginInfo loginInfo, @RequestBody MemberInfoUpdateRequest request) {
        memberService.updateMemberInfo(loginInfo.memberId(), request);
        return ResponseEntity.noContent().build();
    }

    @AuthRequired
    @GetMapping("/summary")
    public ResponseEntity<MyInfoSummaryResponse> getMyInfoSummary(@Login LoginInfo loginInfo) {
        MyInfoSummaryResponse memberInfoSummary = memberService.getMemberInfoSummary(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberInfoSummary);
    }

    @AuthRequired
    @GetMapping("/status")
    public ResponseEntity<AdminActiveStatusResponse> getAdminMemberStatus(@Login LoginInfo loginInfo) {
        boolean isActive = memberService.getAdminMemberActiveStatus(loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new AdminActiveStatusResponse(isActive));
    }
}
