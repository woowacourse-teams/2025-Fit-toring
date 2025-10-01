package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ChatRoomAlreadyExistsException;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import fittoring.mentoring.business.model.ChatRoom;
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
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.service.dto.chat.ChatRoomCreatedInfo;
import fittoring.mentoring.presentation.dto.ChatRoomResponse;
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
@Import({DbCleaner.class, ChatRoomService.class, QueryDslConfig.class, JpaConfiguration.class})
@DataJpaTest
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("채팅방 등록에 성공하면 채팅방 생성 정보를 반환한다.")
    @Test
    void registerChatRoomSuccess() {
        //given
        Member mentor = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개", "가상의카카오오픈채팅");
        em.persist(mentoring);

        Member mentee = new Member("멘티id", "MALE", "김멘티", new Phone("010-1234-1234"), Password.from("password"));
        em.persist(mentee);

        Reservation reservation = new Reservation("content", Status.APPROVED, mentoring, mentee);
        em.persist(reservation);

        //when
        ChatRoomCreatedInfo chatRoomCreatedInfo = chatRoomService.registerChatRoom(reservation);

        //then
        assertThat(chatRoomCreatedInfo.url()).isNotNull();
    }

    @DisplayName("멘티는 채팅방 조회를 할 수 있다.")
    @Test
    void findChatRoomByMentee() {
        //given
        Member mentor = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개", "가상의카카오오픈채팅");
        em.persist(mentoring);

        Image image = new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, ImageVariant.THUMBNAIL, mentoring.getId());
        em.persist(image);

        Member mentee = new Member("멘티id", "MALE", "김멘티", new Phone("010-1234-1234"), Password.from("password"));
        em.persist(mentee);

        Reservation reservation = new Reservation("content", Status.APPROVED, mentoring, mentee);
        em.persist(reservation);

        ChatRoom chatRoom = new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId());
        em.persist(chatRoom);

        //when
        ChatRoomResponse chatRoomResponse = chatRoomService.findChatRoom(mentee.getId(), chatRoom.getId());

        //then
        assertAll(
                () -> assertThat(chatRoomResponse.participant()).isEqualTo(mentor.getName()),
                () -> assertThat(chatRoomResponse.mentorName()).isEqualTo(mentor.getName()),
                () -> assertThat(chatRoomResponse.mentorProfileImageUrl()).isEqualTo(image.getUrl()),
                () -> assertThat(chatRoomResponse.mentoringPrice()).isEqualTo(mentoring.getPrice()),
                () -> assertThat(chatRoomResponse.senderId()).isEqualTo(mentee.getId())
        );
    }

    @DisplayName("멘토는 채팅방 조회를 할 수 있다.")
    @Test
    void findChatRoomByMentor() {
        //given
        Member mentor = new Member(
                "id1",
                "MALE",
                "김트레이너",
                new Phone("010-1234-9048"),
                Password.from("pw"),
                MemberRole.MENTOR
        );
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(
                mentor,
                5000,
                3,
                "컨텐츠컨텐츠",
                "자기소개자기소개",
                "가상의카카오오픈채팅"
        );
        em.persist(mentoring);

        Image image = new Image(
                "멘토링이미지1url",
                ImageType.MENTORING_PROFILE,
                ImageVariant.THUMBNAIL,
                mentoring.getId()
        );
        em.persist(image);

        Member mentee = new Member(
                "멘티id",
                "MALE",
                "김멘티",
                new Phone("010-1234-1234"),
                Password.from("password"),
                MemberRole.MENTEE
        );
        em.persist(mentee);

        Reservation reservation = new Reservation("content", Status.APPROVED, mentoring, mentee);
        em.persist(reservation);

        ChatRoom chatRoom = new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId());
        em.persist(chatRoom);

        //when
        ChatRoomResponse chatRoomResponse = chatRoomService.findChatRoom(mentor.getId(), chatRoom.getId());

        //then
        assertAll(
                () -> assertThat(chatRoomResponse.participant()).isEqualTo(mentee.getName()),
                () -> assertThat(chatRoomResponse.mentorName()).isEqualTo(mentor.getName()),
                () -> assertThat(chatRoomResponse.mentorProfileImageUrl()).isEqualTo(image.getUrl()),
                () -> assertThat(chatRoomResponse.mentoringPrice()).isEqualTo(mentoring.getPrice()),
                () -> assertThat(chatRoomResponse.senderId()).isEqualTo(mentor.getId())
        );
    }

    @DisplayName("존재하지 않는 채팅방을 조회하는 경우 예외가 발생한다.")
    @Test
    void findChatRoom_fail_not_found_chatroom() {
        //given
        Long invalidChatRoomId = -1L;

        //when & then
        assertThatThrownBy(() -> chatRoomService.findChatRoom(1L, invalidChatRoomId))
                .isInstanceOf(ChatRoomNotFoundException.class)
                .hasMessage(BusinessErrorMessage.CHAT_ROOM_NOT_FOUNT.getMessage());
    }

    @DisplayName("동일한 예약으로 두 번 채팅방을 생성하려고 할 때 예외가 발생한다.")
    @Test
    void registerChatRoom_fail_already_exists() {
        //given
        Member mentor = new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개", "가상의카카오오픈채팅");
        em.persist(mentoring);

        Member mentee = new Member("멘티id", "MALE", "김멘티", new Phone("010-1234-1234"), Password.from("password"));
        em.persist(mentee);

        Reservation reservation = new Reservation("content", Status.APPROVED, mentoring, mentee);
        em.persist(reservation);

        chatRoomService.registerChatRoom(reservation);

        //when
        //then
        assertThatThrownBy(() -> chatRoomService.registerChatRoom(reservation))
                .isInstanceOf(ChatRoomAlreadyExistsException.class);
    }

    @DisplayName("채팅방의 멘토나 멘티가 아닌 다른 사용자가 조회를 시도할 때 예외가 발생한다.")
    @Test
    void findChatRoom_fail_unauthorized_access() {
        //given
        Member mentor = new Member("id3", "MALE", "최트레이너", new Phone("010-9999-8888"), Password.from("pw3"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 12000, 2, "테스트컨텐츠", "테스트소개", "카카오톡링크");
        em.persist(mentoring);

        Member mentee = new Member("멘티id3", "MALE", "이멘티", new Phone("010-8888-7777"), Password.from("password3"));
        em.persist(mentee);

        Reservation reservation = new Reservation("content", Status.APPROVED, mentoring, mentee);
        em.persist(reservation);

        ChatRoom chatRoom = new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId());
        em.persist(chatRoom);

        Member stranger = new Member("stranger", "MALE", "남", new Phone("010-0000-0000"), Password.from("pw4"));
        em.persist(stranger);

        //when
        //then
        assertThatThrownBy(() -> chatRoomService.findChatRoom(stranger.getId(), chatRoom.getId()))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class);
    }

    @DisplayName("존재하지 않는 프로필 이미지가 포함된 경우 프로필 이미지 URL은 빈 문자열로 반환된다.")
    @Test
    void findChatRoomNullProfileImage() {
        //given
        Member mentor = new Member("id3", "MALE", "최트레이너", new Phone("010-9999-8888"), Password.from("pw3"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 12000, 2, "테스트컨텐츠", "테스트소개", "카카오톡링크");
        em.persist(mentoring);

        Member mentee = new Member("멘티id3", "MALE", "이멘티", new Phone("010-8888-7777"), Password.from("password3"));
        em.persist(mentee);

        Reservation reservation = new Reservation("content", Status.APPROVED, mentoring, mentee);
        em.persist(reservation);

        ChatRoom chatRoom = new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId());
        em.persist(chatRoom);

        //when
        ChatRoomResponse actual = chatRoomService.findChatRoom(mentee.getId(), chatRoom.getId());

        //then
        assertThat(actual.mentorProfileImageUrl()).isEqualTo("");
    }

    @DisplayName("삭제된 멘토링(Soft Delete)과 연결된 예약으로 채팅방을 생성하면 예외가 발생한다.")
    @Test
    void registerChatRoom_fail_mentoring_soft_deleted() {
        // given
        Member mentor = new Member("id5", "MALE", "삭제멘토", new Phone("010-2222-3333"), Password.from("pw5"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 10000, 3, "삭제된멘토링", "소개", "카카오링크");
        em.persist(mentoring);

        Member mentee = new Member("mentee5", "MALE", "삭제멘티", new Phone("010-4444-5555"), Password.from("pw6"));
        em.persist(mentee);

        Reservation reservation = new Reservation("삭제된 멘토링 예약", Status.APPROVED, mentoring, mentee);
        em.persist(reservation);
        em.clear();

        Mentoring persistedMentoring = em.find(Mentoring.class, mentoring.getId());
        mentoringRepository.delete(persistedMentoring);
        em.flush();

        // when & then
        assertThatThrownBy(() -> chatRoomService.registerChatRoom(reservation))
                .isInstanceOf(MentoringNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
    }

    @DisplayName("채팅방 조회시 승인(APPROVED), 완료(COMPLETED) 이외의 상태인 예약을 조회하면 예외가 발생한다.")
    @Test
    void findChatRoomFailInvalidReservationStatus() {
        // given
        Member mentor = new Member("id6", "MALE", "멘토", new Phone("010-9999-0000"), Password.from("pw"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 10000, 3, "테스트컨텐츠", "테스트소개", "카카오톡링크");
        em.persist(mentoring);

        Member mentee = new Member("mentee6", "MALE", "멘티", new Phone("010-5555-6666"), Password.from("pw2"));
        em.persist(mentee);

        Reservation reservation = new Reservation("대기중 예약", Status.PENDING, mentoring, mentee);
        em.persist(reservation);

        ChatRoom chatRoom = new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId());
        em.persist(chatRoom);

        // when
        // then
        assertThatThrownBy(() -> chatRoomService.findChatRoom(mentee.getId(), chatRoom.getId()))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class)
                .hasMessage(BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage());
    }

    @DisplayName("채팅방 조회시 승인(APPROVED), 완료(COMPLETED) 상태의 예약은 조회 가능하다")
    @Test
    void findChatRoomApprovedOrCompletedReservationStatus() {
        // given
        Member mentor = new Member("id7", "MALE", "멘토", new Phone("010-1234-0000"), Password.from("pw"));
        em.persist(mentor);

        Mentoring mentoring = new Mentoring(mentor, 12000, 2, "컨텐츠", "소개", "링크");
        em.persist(mentoring);

        Member mentee = new Member("mentee7", "MALE", "멘티", new Phone("010-0000-1111"), Password.from("pw2"));
        em.persist(mentee);

        Reservation approvedReservation = new Reservation("승인된 예약", Status.APPROVED, mentoring, mentee);
        em.persist(approvedReservation);

        ChatRoom chatRoom = new ChatRoom(approvedReservation.getId(), mentee.getId(), mentor.getId());
        em.persist(chatRoom);

        // when
        ChatRoomResponse response = chatRoomService.findChatRoom(mentee.getId(), chatRoom.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.senderId()).isEqualTo(mentee.getId());
    }
}
