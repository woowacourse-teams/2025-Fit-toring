package fittoring.application.auth.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.SpringBootTestSupport;
import fittoring.application.chat.presentation.dto.request.ChatMessageRequest;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.ChatMessageService;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.domain.model.ChatRoom;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ChatMessageServiceTest extends SpringBootTestSupport {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @DisplayName("존재하지 않는 채팅방에 대한 저장의 경우 예외가 발생한다.")
    @Test
    void registerMessage() {
        //given
        Long invalidChatRoomId = 999L;
        ChatMessageRequest request = new ChatMessageRequest("cotent", 123155L);
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

        ChatMessageRequest request = new ChatMessageRequest("content", 1234L);
        Long unauthorizedUserId = 999L;

        //when
        //then
        assertThatThrownBy(() ->
                chatMessageService.registerMessage(chatRoom.getId(), request, unauthorizedUserId))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class)
                .hasMessage(BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage());
    }
}
