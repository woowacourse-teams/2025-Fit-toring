package fittoring.mentoring.presentation.api.admin;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.ChatRoomService;
import fittoring.mentoring.presentation.dto.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @AuthRequired
    @GetMapping("/chatrooms/{chatroomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatroomId
    ) {
        ChatRoomResponse chatRoom = chatRoomService.findChatRoom(loginInfo.memberId(), chatroomId);
        return ResponseEntity.ok(chatRoom);
    }
}
