package fittoring.application.chat.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import fittoring.application.chat.presentation.dto.request.ChatImageMessageRequest;
import fittoring.application.chat.presentation.dto.request.ChatTextMessageRequest;
import fittoring.application.chat.presentation.dto.response.ChatMessageAcceptedResponse;
import fittoring.application.chat.service.ChatMessageDispatchService;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.ChatMessageType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class ChatControllerTest {

    private final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
    private final ChatMessageDispatchService chatMessageDispatchService = mock(ChatMessageDispatchService.class);
    private final ChatController chatController = new ChatController(messagingTemplate, chatMessageDispatchService);

    @DisplayName("TEXT endpoint는 텍스트 메시지 dispatch 결과를 채팅방 topic으로 전송한다.")
    @Test
    void chatText() {
        // given
        Long chatRoomId = 1L;
        LoginInfo loginInfo = new LoginInfo(7L);
        ChatTextMessageRequest request = new ChatTextMessageRequest("안녕하세요", 123L);
        LocalDateTime requestedAt = LocalDateTime.of(2026, 6, 26, 12, 0);
        ChatMessageAcceptedResultDto result = new ChatMessageAcceptedResultDto(
                "message-id",
                request.tempId(),
                chatRoomId,
                loginInfo.memberId(),
                request.content(),
                ChatMessageType.TEXT,
                null,
                null,
                requestedAt
        );

        given(chatMessageDispatchService.dispatchText(chatRoomId, request, loginInfo.memberId()))
                .willReturn(result);

        // when
        chatController.chatText(chatRoomId, request, loginInfo);

        // then
        ArgumentCaptor<ChatMessageAcceptedResponse> responseCaptor =
                ArgumentCaptor.forClass(ChatMessageAcceptedResponse.class);
        then(messagingTemplate).should().convertAndSend(
                eq("/topic/chatroom/" + chatRoomId),
                responseCaptor.capture()
        );

        ChatMessageAcceptedResponse response = responseCaptor.getValue();
        assertThat(response).isEqualTo(ChatMessageAcceptedResponse.from(result));
    }

    @DisplayName("IMAGE endpoint는 이미지 메시지 dispatch 결과를 채팅방 topic으로 전송한다.")
    @Test
    void chatImage() {
        // given
        Long chatRoomId = 1L;
        LoginInfo loginInfo = new LoginInfo(7L);
        ChatImageMessageRequest request = new ChatImageMessageRequest("upload-id", 123L);
        LocalDateTime requestedAt = LocalDateTime.of(2026, 6, 26, 12, 0);
        ChatMessageAcceptedResultDto result = new ChatMessageAcceptedResultDto(
                "message-id",
                request.tempId(),
                chatRoomId,
                loginInfo.memberId(),
                null,
                ChatMessageType.IMAGE,
                null,
                "https://bucket.s3.ap-northeast-2.amazonaws.com/image.png?get-presigned",
                requestedAt
        );

        given(chatMessageDispatchService.dispatchImage(chatRoomId, request, loginInfo.memberId()))
                .willReturn(result);

        // when
        chatController.chatImage(chatRoomId, request, loginInfo);

        // then
        ArgumentCaptor<ChatMessageAcceptedResponse> responseCaptor =
                ArgumentCaptor.forClass(ChatMessageAcceptedResponse.class);
        then(messagingTemplate).should().convertAndSend(
                eq("/topic/chatroom/" + chatRoomId),
                responseCaptor.capture()
        );

        ChatMessageAcceptedResponse response = responseCaptor.getValue();
        assertThat(response).isEqualTo(ChatMessageAcceptedResponse.from(result));
    }
}
