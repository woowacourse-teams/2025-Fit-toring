package fittoring.mentoring.presentation.api;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.service.ChatRoomService;
import fittoring.mentoring.presentation.dto.ChatRoomResponse;
import fittoring.mentoring.presentation.dto.chat.response.ChatMessageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chatrooms")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @AuthRequired
    @GetMapping("/{chatroomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatroomId
    ) {
        ChatRoomResponse chatRoom = chatRoomService.findChatRoom(loginInfo.memberId(), chatroomId);
        return ResponseEntity.ok(chatRoom);
    }

    @AuthRequired
    @GetMapping("/{chatroomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatRoomId
    ) {
        List<ChatMessageResponse> response = chatRoomService.findChatMessages(chatRoomId, loginInfo.memberId());
        return ResponseEntity.ok(response);
    }
}
