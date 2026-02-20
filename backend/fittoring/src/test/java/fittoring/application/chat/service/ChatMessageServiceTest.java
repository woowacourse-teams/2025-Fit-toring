package fittoring.application.chat.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.image.presentation.dto.response.ImageUrlResponse;
import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.presentation.dto.response.ChatMessageResponse;
import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.exception.ChatMessageNotImageException;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.domain.model.ChatMessageType;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ChatMessageNotFoundException;
import fittoring.application.exception.ChatRoomNotFoundException;
import fittoring.application.exception.UnauthorizedChatRoomAccessException;
import fittoring.application.image.repository.ImageRepository;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatRoom;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ChatMessageServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PresignedUrlService presignedUrlService;

    @DisplayName("존재하지 않는 채팅방에 대한 저장의 경우 예외가 발생한다")
    @Test
    void registerMessage() {
        //given
        Long invalidChatRoomId = 999L;
        ChatMessageRequest request = new ChatMessageRequest("cotent", 123155L, ChatMessageType.TEXT);
        Long senderId = 1L;

        //when
        //then
        assertThatThrownBy(() ->
                chatMessageService.registerMessage(invalidChatRoomId, request, senderId))
                .isInstanceOf(ChatRoomNotFoundException.class)
                .hasMessage(BusinessErrorMessage.CHAT_ROOM_NOT_FOUND.getMessage());
    }

    @DisplayName("참여자가 아닌 사용자가 메시지를 등록하려 할 때 예외가 발생한다.")
    @Test
    void registerMessageUnauthorizedMember() {
        //given
        Long reservationId = 1L;
        Long menteeId = 1L;
        Long mentorId = 2L;

        ChatRoom chatRoom = new ChatRoom(reservationId, menteeId, mentorId);
        chatRoomRepository.save(chatRoom);

        ChatMessageRequest request = new ChatMessageRequest("content", 1234L, ChatMessageType.TEXT);
        Long unauthorizedUserId = 999L;

        //when
        //then
        assertThatThrownBy(() ->
                chatMessageService.registerMessage(chatRoom.getId(), request, unauthorizedUserId))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class)
                .hasMessage(BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage());
    }

    @DisplayName("이미지 메시지 히스토리 조회 시 썸네일이 존재하면 thumbnailUrl과 originalImageUrl을 모두 반환한다.")
    @Test
    void findChatMessagesWithImageThumbnailExists() {
        //given
        Long menteeId = 1L;
        Long mentorId = 2L;
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, menteeId, mentorId));

        ChatMessage imageMessage = chatMessageRepository.save(FixtureUtil.testImageChatMessage(chatRoom, menteeId));
        imageRepository.save(FixtureUtil.testChatImageDefault(imageMessage));
        imageRepository.save(FixtureUtil.testChatImageThumbnail(imageMessage));

        when(presignedUrlService.issueGetUrlWithThumbnail(anyString(), eq(true)))
                .thenReturn(new ImageUrlResponse("https://presigned-get-url-thumbnail", "https://presigned-get-url-default"));
        when(presignedUrlService.issueGetUrlWithThumbnail(anyString(), eq(false)))
                .thenReturn(new ImageUrlResponse(null, "https://presigned-get-url-default"));

        //when
        ChatMessagePaginationResponse response = chatMessageService.findChatMessages(chatRoom.getId(), menteeId, null);

        //then
        ChatMessageResponse msg = response.chatMessages().getFirst();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(msg.messageType()).isEqualTo(ChatMessageType.IMAGE);
            softly.assertThat(msg.thumbnailUrl()).isEqualTo("https://presigned-get-url-thumbnail");
            softly.assertThat(msg.originalImageUrl()).isEqualTo("https://presigned-get-url-default");
            softly.assertThat(msg.content()).isNull();
        });
    }

    @DisplayName("이미지 메시지 히스토리 조회 시 썸네일이 없으면 thumbnailUrl은 null이고 originalImageUrl만 반환한다.")
    @Test
    void findChatMessagesWithImageThumbnailNotExists() {
        //given
        Long menteeId = 1L;
        Long mentorId = 2L;
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, menteeId, mentorId));

        ChatMessage imageMessage = chatMessageRepository.save(FixtureUtil.testImageChatMessage(chatRoom, menteeId));
        imageRepository.save(FixtureUtil.testChatImageDefault(imageMessage));

        when(presignedUrlService.issueGetUrlWithThumbnail(anyString(), eq(true)))
                .thenReturn(new ImageUrlResponse("https://presigned-get-url-thumbnail", "https://presigned-get-url-default"));
        when(presignedUrlService.issueGetUrlWithThumbnail(anyString(), eq(false)))
                .thenReturn(new ImageUrlResponse(null, "https://presigned-get-url-default"));

        //when
        ChatMessagePaginationResponse response = chatMessageService.findChatMessages(chatRoom.getId(), menteeId, null);

        //then
        ChatMessageResponse msg = response.chatMessages().getFirst();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(msg.messageType()).isEqualTo(ChatMessageType.IMAGE);
            softly.assertThat(msg.thumbnailUrl()).isNull();
            softly.assertThat(msg.originalImageUrl()).isEqualTo("https://presigned-get-url-default");
        });
    }

    @DisplayName("이미지 URL 재발급 시 정상적으로 ChatImageUrlResponse를 반환한다.")
    @Test
    void reissueImageUrlSuccess() {
        //given
        Long menteeId = 1L;
        Long mentorId = 2L;
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, menteeId, mentorId));

        ChatMessage imageMessage = chatMessageRepository.save(FixtureUtil.testImageChatMessage(chatRoom, menteeId));
        imageRepository.save(FixtureUtil.testChatImageDefault(imageMessage));
        imageRepository.save(FixtureUtil.testChatImageThumbnail(imageMessage));

        when(presignedUrlService.issueGetUrlWithThumbnail(anyString(), eq(true)))
                .thenReturn(new ImageUrlResponse("https://presigned-get-url-thumbnail", "https://presigned-get-url-default"));
        when(presignedUrlService.issueGetUrlWithThumbnail(anyString(), eq(false)))
                .thenReturn(new ImageUrlResponse(null, "https://presigned-get-url-default"));

        //when
        ImageUrlResponse response = chatMessageService.reissueImageUrl(chatRoom.getId(), imageMessage.getId(), menteeId);

        //then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.thumbnailUrl()).isEqualTo("https://presigned-get-url-thumbnail");
            softly.assertThat(response.originalImageUrl()).isEqualTo("https://presigned-get-url-default");
        });
    }

    @DisplayName("존재하지 않는 메시지에 대해 이미지 URL 재발급 시 예외가 발생한다.")
    @Test
    void reissueImageUrlMessageNotFound() {
        //given
        Long menteeId = 1L;
        Long mentorId = 2L;
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, menteeId, mentorId));
        Long invalidMessageId = 999L;

        //when //then
        assertThatThrownBy(() ->
                chatMessageService.reissueImageUrl(chatRoom.getId(), invalidMessageId, menteeId))
                .isInstanceOf(ChatMessageNotFoundException.class)
                .hasMessage(BusinessErrorMessage.CHAT_MESSAGE_NOT_FOUND.getMessage());
    }

    @DisplayName("TEXT 타입 메시지에 대해 이미지 URL 재발급 시 예외가 발생한다.")
    @Test
    void reissueImageUrlTextMessage() {
        //given
        Long menteeId = 1L;
        Long mentorId = 2L;
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, menteeId, mentorId));

        ChatMessage textMessage = chatMessageRepository.save(
                new ChatMessage(chatRoom.getId(), menteeId, "안녕하세요"));

        //when //then
        assertThatThrownBy(() ->
                chatMessageService.reissueImageUrl(chatRoom.getId(), textMessage.getId(), menteeId))
                .isInstanceOf(ChatMessageNotImageException.class)
                .hasMessage(BusinessErrorMessage.CHAT_MESSAGE_NOT_IMAGE.getMessage());
    }
}
