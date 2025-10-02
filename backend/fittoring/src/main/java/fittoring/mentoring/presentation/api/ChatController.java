package fittoring.mentoring.presentation.api;

import fittoring.config.auth.LoginInfo;
import fittoring.config.websocket.AuthHandshakeInterceptor;
import fittoring.mentoring.business.service.ChatMessageService;
import fittoring.mentoring.presentation.dto.chat.request.ChatMessageRequest;
import fittoring.mentoring.presentation.dto.chat.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chatroom/{chatRoomId}")
    public void chat(
            @DestinationVariable Long chatRoomId,
            ChatMessageRequest request,
            @Header(AuthHandshakeInterceptor.LOGIN_INFO_KEY) LoginInfo loginInfo
    ) {
        ChatMessageResponse response = chatMessageService.registerMessage(request, loginInfo.memberId());

        messagingTemplate.convertAndSend("/topic/chatroom/" + chatRoomId, response);
    }
}
