package fittoring.mentoring.presentation.api;

import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.MemberService;
import fittoring.mentoring.presentation.dto.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/me")
    public ResponseEntity<MyInfoResponse> getMyInfo(@Login LoginInfo loginInfo) {
        MyInfoResponse memberInfo = memberService.getMemberInfo(loginInfo.memberId());
        return ResponseEntity.ok(memberInfo);
    }
}
