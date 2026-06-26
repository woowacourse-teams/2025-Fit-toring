package fittoring.application.chat.service;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import fittoring.application.chat.presentation.dto.request.ChatTextMessageRequest;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import fittoring.domain.model.ChatMessageType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ChatMessageDispatchServiceTest {

    private final ChatMessagePersistEventPublisher eventPublisher = mock(ChatMessagePersistEventPublisher.class);
    private final ChatMessageDispatchService dispatchService = new ChatMessageDispatchService(
            eventPublisher
    );

    @DisplayName("TEXT 메시지는 content를 그대로 publish 하고 이미지 URL 발급 없이 accepted 된다.")
    @Test
    void textMessageIsAcceptedWithoutPresignedInteraction() {
        // given
        Long chatRoomId = 1L;
        Long senderId = 7L;
        ChatTextMessageRequest request = new ChatTextMessageRequest("안녕하세요", 124L);

        // when
        ChatMessageAcceptedResultDto result = dispatchService.dispatchText(chatRoomId, request, senderId);

        // then
        ArgumentCaptor<ChatMessagePersistEventDto> eventCaptor =
                ArgumentCaptor.forClass(ChatMessagePersistEventDto.class);
        then(eventPublisher).should().publish(eventCaptor.capture());
        ChatMessagePersistEventDto event = eventCaptor.getValue();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.messageType()).isEqualTo(ChatMessageType.TEXT);
            softly.assertThat(result.content()).isEqualTo("안녕하세요");
            softly.assertThat(result.chatRoomId()).isEqualTo(chatRoomId);
            softly.assertThat(result.senderId()).isEqualTo(senderId);
            softly.assertThat(result.tempId()).isEqualTo(request.tempId());
            softly.assertThat(result.originalImageUrl()).isNull();
            softly.assertThat(result.thumbnailUrl()).isNull();
            softly.assertThat(event.messageId()).isEqualTo(result.messageId());
            softly.assertThat(event.chatRoomId()).isEqualTo(chatRoomId);
            softly.assertThat(event.senderId()).isEqualTo(senderId);
            softly.assertThat(event.tempId()).isEqualTo(request.tempId());
            softly.assertThat(event.content()).isEqualTo(request.content());
            softly.assertThat(event.messageType()).isEqualTo(ChatMessageType.TEXT);
        });
    }
}
