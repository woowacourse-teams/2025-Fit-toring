package fittoring.mentoring.business.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.presentation.dto.chat.request.ChatMessageRequest;
import fittoring.util.DbCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({
        DbCleaner.class,
        JpaConfiguration.class,
        QueryDslConfig.class,
        ChatMessageService.class
})
@DataJpaTest
class ChatMessageServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

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
                .hasMessage(BusinessErrorMessage.CHAT_ROOM_NOT_FOUNT.getMessage());
    }

    @DisplayName("참여자가 아닌 사용자가 메시지를 등록하려 할 때 예외가 발생한다.")
    @Test
    void registerMessageUnauthorizedMember() {
        //given
        Long reservationId = 1L;
        Long menteeId = 1L;
        Long mentorId = 2L;

        ChatRoom chatRoom = new ChatRoom(reservationId, menteeId, mentorId);
        em.persist(chatRoom);
        em.persistAndFlush(chatRoom);

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
