package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.repository.ChatRoomRepository;
import fittoring.mentoring.business.service.dto.chat.ChatRoomCreatedInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoomCreatedInfo registerChatRoom(Reservation reservation) {
        ChatRoom chatRoom = new ChatRoom(
                reservation.getId(),
                reservation.getMentee().getId(),
                reservation.getMentoring().getMentor().getId()
        );
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        String url = ChatRoomUrlGenerator.generate(savedChatRoom.getId());
        return new ChatRoomCreatedInfo(url);
    }

    @Transactional(readOnly = true)
    public ChatRoomResponse findChatRoom(Long memberId, Long chatroomId) {
        ChatRoom chatRoom = getChatRoom(chatroomId);
        validateParticipant(memberId, chatRoom);

        Reservation reservation = getReservation(chatRoom);
        validateReservationStatus(reservation);

        Member member = getMember(memberId);

        String participant = getParticipantName(member, reservation);
        String mentorName = reservation.getMentoring().getMentorName();
        Mentoring mentoring = reservation.getMentoring();
        Image profileImage = getImage(mentoring);

        return ChatRoomResponse.of(
                participant,
                mentorName,
                profileImage,
                mentoring.getPrice(),
                memberId
        );
    }

    private ChatRoom getChatRoom(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId)
                .orElseThrow(
                        () -> new ChatRoomNotFoundException(BusinessErrorMessage.CHAT_ROOM_NOT_FOUNT.getMessage())
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
        if (!reservation.isChatRoomAccessibleStatus()) {
            throw new UnauthorizedChatRoomAccessException(
                    BusinessErrorMessage.INVALID_STATUS_CHAT_ROOM_ACCESS.getMessage()
            );
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }

    private Image getImage(Mentoring mentoring) {
        return imageRepository.findByImageTypeAndRelationIdAndImageVariant(
                ImageType.MENTORING_PROFILE,
                mentoring.getId(),
                ImageVariant.THUMBNAIL
        ).orElse(null);
    }

    private String getParticipantName(Member member, Reservation reservation) {
        if (member.isMentee()) {
            return reservation.getMentorName();
        }
        return reservation.getMenteeName();
    }
}
