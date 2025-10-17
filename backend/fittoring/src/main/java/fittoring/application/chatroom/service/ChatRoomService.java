package fittoring.application.chatroom.service;

import fittoring.application.chatroom.service.dto.ChatRoomInfoDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.exception.ReservationNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.chatroom.repository.ChatRoomRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.mentoring.business.exception.ChatRoomAlreadyExistsException;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.application.chatroom.service.dto.ChatRoomCreatedInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomUrlGenerator chatRoomUrlGenerator;
    private final ChatRoomRepository chatRoomRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MentoringRepository mentoringRepository;

    @Transactional
    public ChatRoomCreatedInfo registerChatRoom(Reservation reservation) {
        Mentoring mentoring = reservation.getMentoring();
        validateMentoringExists(mentoring);
        validateReservationExists(reservation);

        ChatRoom chatRoom = new ChatRoom(
                reservation.getId(),
                reservation.getMentee().getId(),
                reservation.getMentor().getId()
        );
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        String url = chatRoomUrlGenerator.generate(savedChatRoom.getId());
        return new ChatRoomCreatedInfo(url);
    }

    private void validateMentoringExists(Mentoring mentoring) {
        mentoringRepository.findById(mentoring.getId())
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage())
                );
    }

    private void validateReservationExists(Reservation reservation) {
        if (chatRoomRepository.existsByReservationId(reservation.getId())) {
            throw new ChatRoomAlreadyExistsException(BusinessErrorMessage.CHAT_ROOM_ALREADY_EXISTS.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ChatRoomInfoDto findChatRoom(Long memberId, Long chatroomId) {
        ChatRoom chatRoom = getChatRoom(chatroomId);
        validateParticipant(memberId, chatRoom);

        Reservation reservation = getReservation(chatRoom);
        validateReservationStatus(reservation);

        Member member = getMember(memberId);
        String opponentName = getOpponentName(member, reservation);

        return new ChatRoomInfoDto(
                reservation.getMentoring().getId(),
                member.getRole(),
                opponentName,
                chatRoom.getStatus()
        );
    }

    private ChatRoom getChatRoom(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId)
                .orElseThrow(
                        () -> new ChatRoomNotFoundException(BusinessErrorMessage.CHAT_ROOM_NOT_FOUND.getMessage())
                );
    }

    private void validateParticipant(Long memberId, ChatRoom chatRoom) {
        if (chatRoom.isNonParticipant(memberId)) {
            throw new fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.UNAUTHORIZED_CHAT_ROOM_ACCESS.getMessage()
            );
        }
    }

    private Reservation getReservation(ChatRoom chatRoom) {
        return reservationRepository.findById(chatRoom.getReservationId())
                .orElseThrow(
                        () -> new ReservationNotFoundException(
                                BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage())
                );
    }

    private void validateReservationStatus(Reservation reservation) {
        if (!reservation.isAccessibleForChatRoom()) {
            throw new fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.INVALID_STATUS_CHAT_ROOM_ACCESS.getMessage()
            );
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }

    private String getOpponentName(Member member, Reservation reservation) {
        if (member.isMentee()) {
            return reservation.getMentorName();
        }
        return reservation.getMenteeName();
    }
}
