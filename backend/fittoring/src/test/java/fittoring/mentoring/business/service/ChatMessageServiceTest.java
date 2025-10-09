package fittoring.mentoring.business.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
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
}
