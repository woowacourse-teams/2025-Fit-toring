package fittoring.application.mentoring.presentation;

import fittoring.application.mentoring.service.ChatRoomFacadeService;
import fittoring.application.mentoring.service.ChatRoomInfoResponse;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomFacadeService chatRoomFacadeService;

    @AuthRequired
    @GetMapping("/chatrooms/{chatroomId}")
    public ResponseEntity<ChatRoomInfoResponse> getChatRoom(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatroomId
    ) {
        ChatRoomInfoResponse response = chatRoomFacadeService.getChatRoom(chatroomId, loginInfo.memberId());
        return ResponseEntity.ok(response);
    }
}
