package fittoring.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.dto.ChatRoomCreatedInfo;
import fittoring.application.chat.service.dto.ChatRoomInfoDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ChatRoomAlreadyExistsException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ChatStatus;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Status;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

class ChatRoomServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ImageRepository imageRepository;

    @DisplayName("채팅방 등록에 성공하면 채팅방 생성 정보를 반환한다.")
    @Test
    void registerChatRoomSuccess() {
        //given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestApprovedReservation(mentoring, mentee));

        //when
        ChatRoomCreatedInfo chatRoomCreatedInfo = chatRoomService.registerChatRoom(reservation);

        //then
        assertThat(chatRoomCreatedInfo.url()).isNotNull();
    }

    @DisplayName("멘티는 채팅방 조회를 할 수 있다.")
    @Test
    void findChatRoomByMentee() {
        //given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        imageRepository.save(FixtureUtil.getTestImageForMentoringProfile(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestApprovedReservation(mentoring, mentee));

        ChatRoom chatRoom = chatRoomRepository.save(
                FixtureUtil.getTestChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        //when
        ChatRoomInfoDto chatRoomInfoDto = chatRoomService.findChatRoom(mentee.getId(), chatRoom.getId());

        //then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(chatRoomInfoDto.mentoringId()).isEqualTo(1L);
            assertThat(chatRoomInfoDto.myRole()).isEqualTo(MemberRole.MENTEE);
            assertThat(chatRoomInfoDto.opponentName()).isEqualTo("멘토이름");
            assertThat(chatRoomInfoDto.status()).isEqualTo(ChatStatus.ACTIVATE);
        });
    }

    @DisplayName("멘토는 채팅방 조회를 할 수 있다.")
    @Test
    void findChatRoomByMentor() {
        //given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        imageRepository.save(FixtureUtil.getTestImageForMentoringProfile(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestApprovedReservation(mentoring, mentee));

        ChatRoom chatRoom = chatRoomRepository.save(
                FixtureUtil.getTestChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        //when
        ChatRoomInfoDto chatRoomInfoDto = chatRoomService.findChatRoom(mentor.getId(), chatRoom.getId());

        //then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(chatRoomInfoDto.mentoringId()).isEqualTo(1L);
            assertThat(chatRoomInfoDto.myRole()).isEqualTo(MemberRole.MENTOR);
            assertThat(chatRoomInfoDto.opponentName()).isEqualTo("이름");
            assertThat(chatRoomInfoDto.status()).isEqualTo(ChatStatus.ACTIVATE);
        });
    }

    @DisplayName("존재하지 않는 채팅방을 조회하는 경우 예외가 발생한다.")
    @Test
    void findChatRoom_fail_not_found_chatroom() {
        //given
        Long invalidChatRoomId = -1L;

        //when & then
        assertThatThrownBy(() -> chatRoomService.findChatRoom(1L, invalidChatRoomId))
                .isInstanceOf(ChatRoomNotFoundException.class)
                .hasMessage(BusinessErrorMessage.CHAT_ROOM_NOT_FOUND.getMessage());
    }

    @DisplayName("동일한 예약으로 두 번 채팅방을 생성하려고 할 때 예외가 발생한다.")
    @Test
    void registerChatRoom_fail_already_exists() {
        //given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestApprovedReservation(mentoring, mentee));

        chatRoomRepository.save(FixtureUtil.getTestChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        //when
        //then
        assertThatThrownBy(() -> chatRoomService.registerChatRoom(reservation))
                .isInstanceOf(ChatRoomAlreadyExistsException.class);
    }

    @DisplayName("채팅방의 멘토나 멘티가 아닌 다른 사용자가 조회를 시도할 때 예외가 발생한다.")
    @Test
    void findChatRoom_fail_unauthorized_access() {
        //given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestApprovedReservation(mentoring, mentee));

        ChatRoom chatRoom = chatRoomRepository.save(
                FixtureUtil.getTestChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        Member stranger = memberRepository.save(FixtureUtil.getTestMentor(1));

        //when
        //then
        assertThatThrownBy(() -> chatRoomService.findChatRoom(stranger.getId(), chatRoom.getId()))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class);
    }

    @DisplayName("삭제된 멘토링(Soft Delete)과 연결된 예약으로 채팅방을 생성하면 예외가 발생한다.")
    @Test
    void registerChatRoom_fail_mentoring_soft_deleted() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestApprovedReservation(mentoring, mentee));

        Mentoring persistedMentoring = mentoringRepository.findById(mentoring.getId())
                .orElse(null);
        mentoringRepository.delete(persistedMentoring);

        // when
        // then
        assertThatThrownBy(() -> chatRoomService.registerChatRoom(reservation))
                .isInstanceOf(MentoringNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
    }

    @DisplayName("채팅방 조회시 승인(APPROVED), 완료(COMPLETED) 이외의 상태인 예약을 조회하면 예외가 발생한다.")
    @Test
    void findChatRoomFailInvalidReservationStatus() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestPendingReservation(mentoring, mentee));

        ChatRoom chatRoom = chatRoomRepository.save(
                FixtureUtil.getTestChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        // when
        // then
        assertThatThrownBy(() -> chatRoomService.findChatRoom(mentee.getId(), chatRoom.getId()))
                .isInstanceOf(UnauthorizedChatRoomAccessException.class)
                .hasMessage(BusinessErrorMessage.INVALID_STATUS_CHAT_ROOM_ACCESS.getMessage());
    }

    @DisplayName("채팅방 조회시 승인(APPROVED), 완료(COMPLETED) 상태의 예약은 조회 가능하다")
    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"APPROVED", "COMPLETE"})
    void findChatRoomApprovedOrCompletedReservationStatus(Status status) {
        // given
        Member mentor = memberRepository.save(FixtureUtil.getTestMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.getTestMentoring(mentor));

        Member mentee = memberRepository.save(FixtureUtil.getTestMentee());

        Reservation reservation = reservationRepository.save(FixtureUtil.getTestApprovedReservation(mentoring, mentee));

        ChatRoom chatRoom = chatRoomRepository.save(
                FixtureUtil.getTestChatRoom(reservation.getId(), mentee.getId(), mentor.getId()));

        // when
        ChatRoomInfoDto response = chatRoomService.findChatRoom(mentee.getId(), chatRoom.getId());

        // then
        SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(response).isNotNull();
                    softly.assertThat(response.status()).isEqualTo(ChatStatus.ACTIVATE);
                }
        );
    }
}
