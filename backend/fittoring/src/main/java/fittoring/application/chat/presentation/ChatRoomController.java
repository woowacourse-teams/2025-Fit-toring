package fittoring.application.chat.presentation;

import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.service.ChatMessageService;
import fittoring.application.chat.service.ChatRoomFacadeService;
import fittoring.application.chat.service.dto.ChatRoomInfoResponse;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/chatrooms")
@RestController
public class ChatRoomController {

    private final ChatRoomFacadeService chatRoomFacadeService;
    private final ChatMessageService chatMessageService;

    @AuthRequired
    @GetMapping("/{chatroomId}")
    public ResponseEntity<ChatRoomInfoResponse> getChatRoom(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatroomId
    ) {
        ChatRoomInfoResponse response = chatRoomFacadeService.getChatRoom(chatroomId, loginInfo.memberId());
        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @GetMapping("/{chatroomId}/messages")
    public ResponseEntity<ChatMessagePaginationResponse> getChatMessages(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatRoomId,
            @RequestParam(required = false) String cursorCode
    ) {
        ChatMessagePaginationResponse response = chatMessageService.findChatMessages(
                chatRoomId,
                loginInfo.memberId(),
                cursorCode
        );
        return ResponseEntity.ok(response);
    }
}
