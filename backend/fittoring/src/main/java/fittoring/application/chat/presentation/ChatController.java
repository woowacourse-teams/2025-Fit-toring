package fittoring.application.chat.presentation;

import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.presentation.dto.response.ChatMessageAcceptedResponse;
import fittoring.application.chat.service.ChatMessageDispatchService;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.config.auth.LoginInfo;
import fittoring.config.websocket.InboundChannelInterceptor;
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
    private final ChatMessageDispatchService chatMessageDispatchService;

    @MessageMapping("/chatroom/{chatRoomId}")
    public void chat(
            @DestinationVariable("chatRoomId") Long chatRoomId,
            @Valid ChatMessageRequest request,
            @Header(InboundChannelInterceptor.LOGIN_INFO_KEY) LoginInfo loginInfo
    ) {
        ChatMessageAcceptedResultDto acceptedResult = chatMessageDispatchService.dispatch(
                chatRoomId,
                request,
                loginInfo.memberId()
        );
        ChatMessageAcceptedResponse response = ChatMessageAcceptedResponse.from(acceptedResult);

        messagingTemplate.convertAndSend("/topic/chatroom/" + chatRoomId, response);
    }
}
