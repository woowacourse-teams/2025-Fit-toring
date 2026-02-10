package fittoring.application.chat.presentation;

import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.presentation.dto.response.ChatMessageResponse;
import fittoring.application.chat.service.ChatMessageService;
import fittoring.config.auth.LoginInfo;
import fittoring.config.websocket.WebSocketAuthHandshakeInterceptor;
import fittoring.config.websocket.WebSocketMetricsListener;
import jakarta.validation.Valid;
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
    private final WebSocketMetricsListener listener;

    @MessageMapping("/chatroom/{chatRoomId}")
    public void chat(
            @DestinationVariable("chatRoomId") Long chatRoomId,
            @Valid ChatMessageRequest request,
            @Header(WebSocketAuthHandshakeInterceptor.LOGIN_INFO_KEY) LoginInfo loginInfo
    ) {
        listener.incrementInboundMessage();
        ChatMessageResponse response = chatMessageService.registerMessage(chatRoomId, request, loginInfo.memberId());

        messagingTemplate.convertAndSend("/topic/chatroom/" + chatRoomId, response);
        listener.incrementOutboundMessage();
    }
}
