package fittoring.integration.mentoring.api;

import fittoring.mentoring.business.model.ChatMessage;
import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.ChatMessageRepository;
import fittoring.mentoring.business.repository.ChatRoomRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.mentoring.presentation.dto.chat.response.ChatMessagePaginationResponse;
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

        String accessToken = jwtProvider.createAccessToken(mentee.getId());

        //when
        ChatMessagePaginationResponse firstResponse = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("chatMessage/get-chatMessage-page-success-first"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .queryParam("sortKey", "CREATED_AT")
                .when()
                .get("/chatrooms/" + chatRoom.getId() + "/messages")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ChatMessagePaginationResponse.class);

        //then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(firstResponse.chatMessages()).hasSize(10);
            softly.assertThat(firstResponse.hasNext()).isTrue();
            softly.assertThat(firstResponse.nextCursorCode()).isNotNull();
        });
    }
}
