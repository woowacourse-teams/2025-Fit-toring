package fittoring.application.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.chat.presentation.dto.response.ChatImagePresignedResponse;
import fittoring.application.chat.repository.ChatImageUploadTicketRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.application.image.presentation.dto.response.PresignedIssueResponse;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.image.service.dto.IssuedPresignedDto;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ImageExtension;
import fittoring.domain.model.ImageType;
import java.time.Duration;
import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ChatImageUploadServiceTest {

    private static final long TTL_SECONDS = 300L;

    private final ChatRoomService chatRoomService = mock(ChatRoomService.class);
    private final PresignedUrlService presignedUrlService = mock(PresignedUrlService.class);
    private final ChatImageUploadTicketRepository ticketRepository = mock(ChatImageUploadTicketRepository.class);
    private final ChatImageUploadService chatImageUploadService = new ChatImageUploadService(
            chatRoomService,
            presignedUrlService,
            ticketRepository,
            TTL_SECONDS
    );

    @DisplayName("채팅방 참여자는 presigned URL과 uploadId를 발급받고, Redis 티켓이 memberId/chatRoomId/s3Key와 TTL로 저장된다.")
    @Test
    void issueForParticipant() {
        // given
        Long chatRoomId = 1L;
        Long memberId = 7L;
        ImageExtension extension = ImageExtension.PNG;
        String key = "fit-toring/prod/chat-image/default/new-uuid.png";
        String putPresignedUrl = "https://bucket.s3.ap-northeast-2.amazonaws.com/.../new-uuid.png?put-presigned";
        LocalDateTime expiresAt = LocalDateTime.of(2026, 6, 24, 22, 5);

        given(chatRoomService.getAccessibleChatRoom(chatRoomId, memberId)).willReturn(mock(ChatRoom.class));
        given(presignedUrlService.issuePresignedUrl(any(IssuedPresignedDto.class)))
                .willReturn(new PresignedIssueResponse(putPresignedUrl, key, expiresAt));

        // when
        ChatImagePresignedResponse response = chatImageUploadService.issue(chatRoomId, memberId, extension);

        // then
        ArgumentCaptor<IssuedPresignedDto> dtoCaptor = ArgumentCaptor.forClass(IssuedPresignedDto.class);
        then(presignedUrlService).should().issuePresignedUrl(dtoCaptor.capture());

        ArgumentCaptor<String> uploadIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
        then(ticketRepository).should().create(
                uploadIdCaptor.capture(),
                eq(memberId),
                eq(chatRoomId),
                eq(key),
                ttlCaptor.capture()
        );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(dtoCaptor.getValue().imageType()).isEqualTo(ImageType.CHAT);
            softly.assertThat(dtoCaptor.getValue().extension()).isEqualTo(extension);
            softly.assertThat(response.uploadId()).isNotBlank();
            softly.assertThat(response.uploadId()).isEqualTo(uploadIdCaptor.getValue());
            softly.assertThat(response.presignedUrl()).isEqualTo(putPresignedUrl);
            softly.assertThat(response.expiresAt()).isEqualTo(expiresAt);
            softly.assertThat(ttlCaptor.getValue()).isEqualTo(Duration.ofSeconds(TTL_SECONDS));
        });
    }

    @DisplayName("채팅방 비참여자는 presigned URL을 발급받지 못하고 티켓도 생성되지 않는다.")
    @Test
    void rejectForNonParticipant() {
        // given
        Long chatRoomId = 1L;
        Long memberId = 999L;
        given(chatRoomService.getAccessibleChatRoom(chatRoomId, memberId))
                .willThrow(new UnauthorizedChatRoomAccessException(
                        BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage()));

        // when // then
        assertThatThrownBy(() -> chatImageUploadService.issue(chatRoomId, memberId, ImageExtension.PNG))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class)
                .hasMessage(BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage());

        then(presignedUrlService).shouldHaveNoInteractions();
        then(ticketRepository).shouldHaveNoInteractions();
    }
}
