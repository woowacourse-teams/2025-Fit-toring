package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.presentation.dto.response.ChatRoomInfoResponse;
import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;

import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Category;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ChatStatus;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Status;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import fittoring.application.chat.presentation.dto.response.ChatRoomPreviewResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;

class ChatRoomIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("채팅방 정보를 조회할 수 있다.")
    @Test
    void findChatRoom() {
        //given
        Member mentor = memberRepository.save(
                new Member(
                        "id1",
                        Gender.MALE,
                        "김트레이너",
                        new Phone("010-1234-9048"),
                        Password.from("pw"),
                        MemberRole.MENTOR
                ));

        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        Mentoring mentoring = mentoringRepository.save(new Mentoring(mentor, 1000, 3, "내용", "자기소개"));

        Category category1 = new Category("근육증가");
        Category category2 = new Category("다이어트");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, ImageVariant.THUMBNAIL, mentoring.getId(),
                "baseName");
        imageRepository.save(image1);

        Member mentee = memberRepository.save(
                new Member(
                        "id2",
                        Gender.MALE,
                        "김멘티",
                        new Phone("010-2345-6789"),
                        Password.from("pw")
                ));

        Reservation reservation = reservationRepository.save(new Reservation("예약", Status.APPROVED, mentoring, mentee));

        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        //when
        ChatRoomInfoResponse response = RestAssured
                .given(spec)
                .log().all().contentType(ContentType.JSON)
                .filter(documentWithTag("chat_room/get-chatroom-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("채팅")
                                .summary("채팅방 정보 조회")
                                .description("채팅방의 상세 정보를 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("ChatRoomInfoResponse"))
                                .responseFields(
                                        fieldWithPath("mentorName")
                                                .type(JsonFieldType.STRING)
                                                .description("멘토 이름"),
                                        fieldWithPath("price")
                                                .type(JsonFieldType.NUMBER)
                                                .description("멘토링 가격"),
                                        fieldWithPath("profileImageUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("멘토링 프로필 이미지 URL").optional(),
                                        fieldWithPath("mentoringId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("멘토링 id"),
                                        fieldWithPath("myRole")
                                                .type(JsonFieldType.STRING)
                                                .description("내 역할 (MENTOR | MENTEE)"),
                                        fieldWithPath("opponentName")
                                                .type(JsonFieldType.STRING).description("상대방 이름"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅방 상태 (ACTIVATE, DEACTIVATE)")
                                )
                                .build())))
                .cookie("accessToken", accessToken)
                .when()
                .get("/chatrooms/{chatRoomId}", chatRoom.getId())
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ChatRoomInfoResponse.class);

        //then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.mentorName()).isEqualTo("김트레이너");
            softAssertions.assertThat(response.price()).isEqualTo(1000);
            softAssertions.assertThat(response.profileImageUrl()).isEqualTo("멘토링이미지1url");
            softAssertions.assertThat(response.opponentName()).isEqualTo("김멘티");
            softAssertions.assertThat(response.status()).isEqualTo(ChatStatus.ACTIVATE);
        });
    }

    @DisplayName("채팅방 정보에 멘토링 썸네일이 없는 경우 이미지는 null로 조회횐다.")
    @Test
    void findChatRoomNotMentoringProfileImage() {
        //given
        Member mentor = memberRepository.save(
                new Member(
                        "id1",
                        Gender.MALE,
                        "김트레이너",
                        new Phone("010-1234-9048"),
                        Password.from("pw"),
                        MemberRole.MENTOR
                ));

        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        Mentoring mentoring = mentoringRepository.save(new Mentoring(mentor, 1000, 3, "내용", "자기소개"));

        Category category1 = new Category("근육증가");
        Category category2 = new Category("다이어트");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Member mentee = memberRepository.save(
                new Member(
                        "id2",
                        Gender.MALE,
                        "김멘티",
                        new Phone("010-2345-6789"),
                        Password.from("pw")
                ));

        Reservation reservation = reservationRepository.save(new Reservation("예약", Status.APPROVED, mentoring, mentee));

        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        //when
        ChatRoomInfoResponse response = RestAssured
                .given(spec)
                .log().all().contentType(ContentType.JSON)
                .filter(documentWithTag("chat_room/get-chatroom-success-non-mentoring-profile-image",
                        resource(ResourceSnippetParameters.builder()
                                .tag("채팅")
                                .build())))
                .cookie("accessToken", accessToken)
                .when()
                .get("/chatrooms/{chatRoomId}", chatRoom.getId())
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ChatRoomInfoResponse.class);

        //then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.mentorName()).isEqualTo("김트레이너");
            softAssertions.assertThat(response.price()).isEqualTo(1000);
            softAssertions.assertThat(response.profileImageUrl()).isNull();
            softAssertions.assertThat(response.opponentName()).isEqualTo("김멘티");
            softAssertions.assertThat(response.status()).isEqualTo(ChatStatus.ACTIVATE);
        });
    }

    @DisplayName("채팅방 메세지 기록을 조회할 수 있다.")
    @Test
    void findChatMessage() {
        //given
        Member mentee = memberRepository.save(
                new Member("id", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));

        Member mentor = memberRepository.save(
                new Member("id1", Gender.MALE, "멘토1", new Phone("010-1234-5678"), Password.from("pw")));

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

        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        //when
        ChatMessagePaginationResponse firstResponse = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("chatMessage/get-chatMessage-page-success-first",
                        resource(ResourceSnippetParameters.builder()
                                .tag("채팅")
                                .summary("채팅 메시지 조회")
                                .description("채팅방의 메시지 기록을 페이징하여 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("ChatMessagePaginationResponse"))
                                .responseFields(
                                        fieldWithPath("chatMessages").type(JsonFieldType.ARRAY)
                                                .description("채팅 메시지 목록"),
                                        fieldWithPath("chatMessages[].chatMessageId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("채팅 메시지 ID"),
                                        fieldWithPath("chatMessages[].tempId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("임시 메시지 ID")
                                                .optional(),
                                        fieldWithPath("chatMessages[].chatRoomId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("채팅방 ID"),
                                        fieldWithPath("chatMessages[].senderId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("보낸 사람 ID"),
                                        fieldWithPath("chatMessages[].content")
                                                .type(JsonFieldType.STRING)
                                                .description("메시지 내용"),
                                        fieldWithPath("chatMessages[].createdAt")
                                                .type(JsonFieldType.STRING)
                                                .description("메시지 생성 시각"),
                                        fieldWithPath("hasNext")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("다음 페이지 존재 여부"),
                                        fieldWithPath("nextCursorCode")
                                                .type(JsonFieldType.STRING)
                                                .description("다음 페이지 커서 코드").optional()
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .get("/chatrooms/{chatRoomId}/messages", chatRoom.getId())
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

    @DisplayName("채팅방 미리보기 목록을 조회할 수 있다.")
    @Test
    void getChatRoomPreviews() {
        // given
        // 시나리오: '멘티' 사용자가 로그인하여 자신이 속한 채팅방 목록을 조회합니다.
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Image image = imageRepository.save(FixtureUtil.testImageForMentoringProfileThumbnail(mentoring));
        Reservation reservation = reservationRepository.save(FixtureUtil.testApprovedReservation(mentoring, mentee));
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(reservation, mentor, mentee));
        ChatMessage chatMessage = chatMessageRepository.save(FixtureUtil.testChatMessage(chatRoom, mentee));

        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        // when
        var response = RestAssured
                .given(spec).log().all()
                .accept("application/json")
                .filter(documentWithTag("chat_room/get-chatroom-previews-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("채팅")
                                .summary("채팅방 미리보기 목록 조회")
                                .description("사용자가 참여하고 있는 채팅방의 미리보기 목록을 조회합니다. 성공 시 200 OK와 함께 채팅방 미리보기 정보 배열을 반환합니다.")
                                .responseSchema(Schema.schema("ChatRoomPreviewResponseList"))
                                .responseFields(
                                        fieldWithPath("[].chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                        fieldWithPath("[].profileImageUrl").type(JsonFieldType.STRING).description("멘토링 프로필 이미지 URL").optional(),
                                        fieldWithPath("[].opponentName").type(JsonFieldType.STRING).description("상대방 이름"),
                                        fieldWithPath("[].reservationStatus").type(JsonFieldType.STRING).description("예약 상태 (APPROVED, PENDING 등)"),
                                        fieldWithPath("[].lastChatContent").type(JsonFieldType.STRING).description("마지막 메시지 내용 (메시지가 없으면 null)").optional(),
                                        fieldWithPath("[].lastChatCreatedAt").type(JsonFieldType.STRING).description("마지막 메시지 생성 시각 (메시지가 없으면 null, yyyy-MM-dd'T'HH:mm:ss 형식)").optional()
                                )
                                .build())))
                .cookie("accessToken", accessToken)
                .when()
                .get("/chatrooms")
                .then().log().all()
                .statusCode(200)
                .extract();

        // then
        List<ChatRoomPreviewResponse> previewResponses = response.as(new TypeRef<>() {});

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(previewResponses).hasSize(1);
            ChatRoomPreviewResponse firstPreview = previewResponses.getFirst();
            softly.assertThat(firstPreview.chatRoomId()).isEqualTo(chatRoom.getId());
            softly.assertThat(firstPreview.opponentName()).isEqualTo(mentor.getName());
            softly.assertThat(firstPreview.profileImageUrl()).isEqualTo(image.getUrl());
            softly.assertThat(firstPreview.lastChatContent()).isEqualTo(chatMessage.getContent());
            softly.assertThat(firstPreview.reservationStatus()).isEqualTo(reservation.getStatus());
        });
    }
}
