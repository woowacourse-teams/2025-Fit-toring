package fittoring.integration;

import fittoring.application.auth.service.JwtProvider;
import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Member;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ChatRoomControllerTest extends AbstractApiDocumentationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @DisplayName("채팅방 메세지 기록을 조회할 수 있다.")
    @Test
    void findChatMessage() {
        //given
        Member mentee = memberRepository.save(
                new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

        Member mentor = memberRepository.save(
                new Member("id1", "MALE", "멘토1", new Phone("010-1234-5678"), Password.from("pw")));

        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(1L, mentor.getId(), mentee.getId()));

        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content1"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content2"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content3"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content4"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content5"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content6"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content7"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content8"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content9"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content10"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content11"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content12"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content13"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content14"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content15"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content16"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content17"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content18"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content19"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content20"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentor.getId(), "content21"));
        chatMessageRepository.save(new ChatMessage(chatRoom.getId(), mentee.getId(), "content22"));

        String accessToken = jwtProvider.createAccessToken(mentee.getId());

        //when
        ChatMessagePaginationResponse firstResponse = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("chatMessage/get-chatMessage-page-success-first"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/chatrooms/" + chatRoom.getId() + "/messages")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ChatMessagePaginationResponse.class);

        //then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(firstResponse.chatMessages()).hasSize(20);
            softly.assertThat(firstResponse.hasNext()).isTrue();
            softly.assertThat(firstResponse.nextCursorCode()).isNotNull();
        });
    }
}
