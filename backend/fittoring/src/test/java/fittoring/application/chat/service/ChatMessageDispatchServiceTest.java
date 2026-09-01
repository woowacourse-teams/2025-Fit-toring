package fittoring.application.chat.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import fittoring.application.chat.presentation.dto.request.ChatImageMessageRequest;
import fittoring.application.chat.presentation.dto.request.ChatTextMessageRequest;
import fittoring.application.chat.repository.ChatImageUploadTicketRepository;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.exception.InvalidChatImageUploadTicketException;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.domain.model.ChatMessageType;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ChatMessageDispatchServiceTest {

    private final ChatMessagePersistEventPublisher eventPublisher = mock(ChatMessagePersistEventPublisher.class);
    private final ChatImageUploadTicketRepository ticketRepository = mock(ChatImageUploadTicketRepository.class);
    private final PresignedUrlService presignedUrlService = mock(PresignedUrlService.class);
    private final ChatMessageDispatchService dispatchService = new ChatMessageDispatchService(
            eventPublisher,
            ticketRepository,
            presignedUrlService
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

    @DisplayName("IMAGE 메시지는 티켓을 소비해 얻은 s3Key를 content로 publish 하고 GET presigned URL을 포함해 accepted 된다.")
    @Test
    void imageMessageIsAcceptedWithTicketKey() {
        // given
        Long chatRoomId = 1L;
        Long senderId = 7L;
        String uploadId = "upload-id";
        String s3Key = "local/chat-image/default/server-issued-uuid.png";
        String getUrl = "https://bucket.s3.ap-northeast-2.amazonaws.com/" + s3Key + "?get-presigned";
        ChatImageMessageRequest request = new ChatImageMessageRequest(uploadId, 123L);

        given(ticketRepository.consume(uploadId, senderId, chatRoomId)).willReturn(Optional.of(s3Key));
        given(presignedUrlService.isObjectExistsFromKey(s3Key)).willReturn(true);
        given(presignedUrlService.issueGetPresignedUrl(s3Key)).willReturn(getUrl);

        // when
        ChatMessageAcceptedResultDto result = dispatchService.dispatchImage(chatRoomId, request, senderId);

        // then
        ArgumentCaptor<ChatMessagePersistEventDto> eventCaptor =
                ArgumentCaptor.forClass(ChatMessagePersistEventDto.class);
        then(eventPublisher).should().publish(eventCaptor.capture());
        ChatMessagePersistEventDto event = eventCaptor.getValue();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.messageType()).isEqualTo(ChatMessageType.IMAGE);
            softly.assertThat(result.content()).isNull();
            softly.assertThat(result.chatRoomId()).isEqualTo(chatRoomId);
            softly.assertThat(result.senderId()).isEqualTo(senderId);
            softly.assertThat(result.tempId()).isEqualTo(request.tempId());
            softly.assertThat(result.originalImageUrl()).isEqualTo(getUrl);
            softly.assertThat(result.thumbnailUrl()).isNull();
            softly.assertThat(event.content()).isEqualTo(s3Key);
            softly.assertThat(event.messageType()).isEqualTo(ChatMessageType.IMAGE);
            softly.assertThat(event.tempId()).isEqualTo(request.tempId());
        });
        // 회귀 방지(단계 6): 채팅 이미지 전송 경로는 URL 기반 검증을 쓰지 않고 key 기반 검증만 사용한다.
        then(presignedUrlService).should(never()).isObjectExistsFromUrl(anyString());
    }

    @DisplayName("티켓 검증에 실패(부재/다른 회원/다른 채팅방)하면 거부되고 S3 조회/publish 가 일어나지 않는다.")
    @Test
    void imageRejectedWhenTicketInvalid() {
        // given
        Long chatRoomId = 1L;
        Long senderId = 7L;
        String uploadId = "upload-id";
        ChatImageMessageRequest request = new ChatImageMessageRequest(uploadId, 123L);

        given(ticketRepository.consume(uploadId, senderId, chatRoomId)).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> dispatchService.dispatchImage(chatRoomId, request, senderId))
                .isInstanceOf(InvalidChatImageUploadTicketException.class)
                .hasMessage(BusinessErrorMessage.INVALID_CHAT_IMAGE_UPLOAD_TICKET.getMessage());
        then(presignedUrlService).shouldHaveNoInteractions();
        then(eventPublisher).shouldHaveNoInteractions();
    }

    @DisplayName("티켓의 s3Key가 S3에 존재하지 않으면 거부되고 publish 가 일어나지 않는다.")
    @Test
    void imageRejectedWhenS3ObjectMissing() {
        // given
        Long chatRoomId = 1L;
        Long senderId = 7L;
        String uploadId = "upload-id";
        String s3Key = "local/chat-image/default/server-issued-uuid.png";
        ChatImageMessageRequest request = new ChatImageMessageRequest(uploadId, 123L);

        given(ticketRepository.consume(uploadId, senderId, chatRoomId)).willReturn(Optional.of(s3Key));
        given(presignedUrlService.isObjectExistsFromKey(s3Key)).willReturn(false);

        // when // then
        assertThatThrownBy(() -> dispatchService.dispatchImage(chatRoomId, request, senderId))
                .isInstanceOf(ImageNotFoundException.class)
                .hasMessage(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
        then(eventPublisher).shouldHaveNoInteractions();
    }
}
