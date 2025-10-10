package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ChatRoomAlreadyExistsException;
import fittoring.mentoring.business.exception.ChatRoomNotFoundException;
import fittoring.mentoring.business.exception.MemberNotFoundException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.UnauthorizedChatRoomAccessException;
import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.repository.ChatRoomRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.service.dto.chat.ChatRoomCreatedInfo;
import fittoring.mentoring.presentation.dto.ChatRoomInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

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
                reservation.getMentoring().getMentor().getId()
        );
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        String url = ChatRoomUrlGenerator.generate(savedChatRoom.getId());
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
        if (!chatRoom.hasParticipant(memberId)) {
            throw new UnauthorizedChatRoomAccessException(
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
            throw new UnauthorizedChatRoomAccessException(
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
