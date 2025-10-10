package fittoring.mentoring.presentation.api;

import fittoring.integration.mentoring.api.AbstractApiDocumentationTest;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.business.model.ChatStatus;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.ImageVariant;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.ChatRoomRepository;
import fittoring.mentoring.business.repository.ImageRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.ChatRoomInfoResponse;
import fittoring.mentoring.business.service.JwtProvider;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
                        "MALE",
                        "김트레이너",
                        new Phone("010-1234-9048"),
                        Password.from("pw"),
                        MemberRole.MENTOR
                ));

        String accessToken = jwtProvider.createAccessToken(mentor.getId());

        Mentoring mentoring = mentoringRepository.save(new Mentoring(mentor, 1000, 3, "내용", "자기소개", "chatUrl"));

        Category category1 = new Category("근육증가");
        Category category2 = new Category("다이어트");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, ImageVariant.THUMBNAIL, mentoring.getId());
        imageRepository.save(image1);

        Member mentee = memberRepository.save(
                new Member(
                        "id2",
                        "MALE",
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
                .filter(documentWithTag("chat_room/get-chatroom-success"))
                .cookie("accessToken", accessToken)
                .when()
                .get("/chatrooms/" + chatRoom.getId())
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ChatRoomInfoResponse.class);

        //then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.mentoringInfoDto().mentorName()).isEqualTo("김트레이너");
            softAssertions.assertThat(response.mentoringInfoDto().price()).isEqualTo(1000);
            softAssertions.assertThat(response.mentoringInfoDto().profileImageUrl()).isEqualTo("멘토링이미지1url");
            softAssertions.assertThat(response.chatRoomInfoDto().opponentName()).isEqualTo("김멘티");
            softAssertions.assertThat(response.chatRoomInfoDto().status()).isEqualTo(ChatStatus.ACTIVATE.name());
        });
    }

    @DisplayName("채팅방 정보에 멘토링 썸네일이 없는 경우 이미지는 null로 조회횐다.")
    @Test
    void findChatRoomNotMentoringProfileImage() {
        //given
        Member mentor = memberRepository.save(
                new Member(
                        "id1",
                        "MALE",
                        "김트레이너",
                        new Phone("010-1234-9048"),
                        Password.from("pw"),
                        MemberRole.MENTOR
                ));

        String accessToken = jwtProvider.createAccessToken(mentor.getId());

        Mentoring mentoring = mentoringRepository.save(new Mentoring(mentor, 1000, 3, "내용", "자기소개", "chatUrl"));

        Category category1 = new Category("근육증가");
        Category category2 = new Category("다이어트");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Member mentee = memberRepository.save(
                new Member(
                        "id2",
                        "MALE",
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
                .filter(documentWithTag("chat_room/get-chatroom-success-non-mentoring-profile-image"))
                .cookie("accessToken", accessToken)
                .when()
                .get("/chatrooms/" + chatRoom.getId())
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ChatRoomInfoResponse.class);

        //then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.mentoringInfoDto().mentorName()).isEqualTo("김트레이너");
            softAssertions.assertThat(response.mentoringInfoDto().price()).isEqualTo(1000);
            softAssertions.assertThat(response.mentoringInfoDto().profileImageUrl()).isNull();
            softAssertions.assertThat(response.chatRoomInfoDto().opponentName()).isEqualTo("김멘티");
            softAssertions.assertThat(response.chatRoomInfoDto().status()).isEqualTo(ChatStatus.ACTIVATE.name());
        });
    }
}
