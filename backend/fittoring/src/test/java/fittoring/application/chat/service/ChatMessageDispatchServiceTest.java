package fittoring.application.chat.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.service.dto.ChatMessageAcceptedResultDto;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ImageNotFoundException;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.domain.model.ChatMessageType;
import fittoring.infrastructure.image.KeyBuilder;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChatMessageDispatchServiceTest {

    private final ChatMessagePersistEventPublisher eventPublisher = mock(ChatMessagePersistEventPublisher.class);
    private final PresignedUrlService presignedUrlService = mock(PresignedUrlService.class);
    private final KeyBuilder keyBuilder = mock(KeyBuilder.class);
    private final ChatMessageDispatchService dispatchService = new ChatMessageDispatchService(
            eventPublisher,
            presignedUrlService,
            keyBuilder
    );

    @DisplayName("현재 구조: S3에 객체가 존재하기만 하면 다른 채팅방 소유의 key라도 이미지 메시지로 accepted 된다. (IDOR 취약 흐름 고정)")
    @Test
    void imageAcceptedWhenS3ObjectExistsEvenForForeignRoomKey() {
        // given
        Long myChatRoomId = 1L;
        Long senderId = 7L;
        // 공격자가 지목한, 다른 채팅방 소유의 비공개 이미지 URL/key
        String foreignImageUrl = "https://bucket.s3.ap-northeast-2.amazonaws.com/fit-toring/prod/chat-image/default/victim-uuid.png";
        String extractedKey = "fit-toring/prod/chat-image/default/victim-uuid.png";
        String issuedGetUrl = "https://bucket.s3.ap-northeast-2.amazonaws.com/fit-toring/prod/chat-image/default/victim-uuid.png?get-presigned";
        ChatMessageRequest request = new ChatMessageRequest(foreignImageUrl, 123L, ChatMessageType.IMAGE);

        given(presignedUrlService.isObjectExistsFromUrl(foreignImageUrl)).willReturn(true);
        given(keyBuilder.extractKeyFromUrl(foreignImageUrl)).willReturn(extractedKey);
        given(presignedUrlService.issueGetPresignedUrl(extractedKey)).willReturn(issuedGetUrl);

        // when
        ChatMessageAcceptedResultDto result = dispatchService.dispatch(myChatRoomId, request, senderId);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.messageType()).isEqualTo(ChatMessageType.IMAGE);
            softly.assertThat(result.chatRoomId()).isEqualTo(myChatRoomId);
            softly.assertThat(result.senderId()).isEqualTo(senderId);
            softly.assertThat(result.originalImageUrl()).isEqualTo(issuedGetUrl);
        });
        // 서버는 chatRoomId와 key 발급 기록의 연결을 전혀 검증하지 않고 publish 한다.
        then(eventPublisher).should().publish(any(ChatMessagePersistEventDto.class));
    }

    @DisplayName("이미지 URL이 S3에 존재하지 않으면 ImageNotFoundException이 발생하고 publish 되지 않는다.")
    @Test
    void imageRejectedWhenS3ObjectMissing() {
        // given
        String imageUrl = "https://bucket.s3.ap-northeast-2.amazonaws.com/fit-toring/prod/chat-image/default/missing.png";
        ChatMessageRequest request = new ChatMessageRequest(imageUrl, 123L, ChatMessageType.IMAGE);

        given(presignedUrlService.isObjectExistsFromUrl(imageUrl)).willReturn(false);

        // when // then
        assertThatThrownBy(() -> dispatchService.dispatch(1L, request, 7L))
                .isInstanceOf(ImageNotFoundException.class)
                .hasMessage(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
        then(eventPublisher).shouldHaveNoInteractions();
    }

    @DisplayName("TEXT 메시지는 content를 그대로 publish 하고 이미지 URL 발급 없이 accepted 된다.")
    @Test
    void textMessageIsAcceptedWithoutPresignedInteraction() {
        // given
        ChatMessageRequest request = new ChatMessageRequest("안녕하세요", 124L, ChatMessageType.TEXT);

        // when
        ChatMessageAcceptedResultDto result = dispatchService.dispatch(1L, request, 7L);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.messageType()).isEqualTo(ChatMessageType.TEXT);
            softly.assertThat(result.content()).isEqualTo("안녕하세요");
            softly.assertThat(result.originalImageUrl()).isNull();
            softly.assertThat(result.thumbnailUrl()).isNull();
        });
        then(eventPublisher).should().publish(any(ChatMessagePersistEventDto.class));
        then(presignedUrlService).shouldHaveNoInteractions();
    }
}
