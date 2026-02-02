package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.response.ChatRoomInfoResponse;
import fittoring.application.chat.presentation.dto.response.ChatRoomPreviewResponse;
import fittoring.application.chat.service.dto.ChatRoomInfoDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DataIntegrityException;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.service.MemberService;
import fittoring.application.mentoring.service.MentoringService;
import fittoring.application.mentoring.service.dto.ChatRoomMentoringInfoDto;
import fittoring.application.reservation.service.ReservationService;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Reservation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomFacadeService {

    private final ChatRoomService chatRoomService;
    private final ReservationService reservationService;
    private final MentoringService mentoringService;
    private final MemberService memberService;
    private final ChatMessageService chatMessageService;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public ChatRoomInfoResponse getChatRoom(Long chatroomId, Long memberId) {
        ChatRoomInfoDto chatRoomInfo = chatRoomService.findChatRoom(chatroomId, memberId);
        ChatRoomMentoringInfoDto mentoringInfo = mentoringService.findMentoringInfoForChatRoom(
                chatRoomInfo.mentoringId()
        );
        return ChatRoomInfoResponse.of(mentoringInfo, chatRoomInfo);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomPreviewResponse> getChatRoomPreviews(Long memberId) {
        List<ChatRoom> chatRooms = chatRoomService.findAllByMemberId(memberId);
        if (chatRooms.isEmpty()) {
            return List.of();
        }

        Map<Long, ChatMessage> roomIdLastMessageMapping = chatMessageService.findChatRoomLastChatMessageMapping(
                chatRooms);

        List<Reservation> reservations = reservationService.findReservationsWithMentoring(chatRooms);
        Map<Long, Reservation> reservationsById = reservationService.getReservationMapping(reservations);

        List<Long> opponentsIds = chatRoomService.getOpponentIds(memberId, chatRooms);
        Map<Long, String> nameByMemberId = memberService.findNameMapping(opponentsIds);

        List<Long> mentoringIds = reservationService.getMentoringIds(reservations);
        Map<Long, String> mentoringIdProfileImageUrlMapping = imageService.getRelationIdThumbnailUrlMapping(
                ImageType.MENTORING_PROFILE, mentoringIds);

        return chatRooms.stream()
                .map(
                        room -> {
                            Reservation reservation = getReservation(room, reservationsById);
                            Long mentoringId = reservation.getMentoring().getId();
                            String opponentName = getOpponentName(memberId, room, nameByMemberId);
                            String profileImageUrl = mentoringIdProfileImageUrlMapping.get(mentoringId);
                            ChatMessage lastMessage = roomIdLastMessageMapping.get(room.getId());

                            if (lastMessage == null) {
                                return ChatRoomPreviewResponse.of(room.getId(),
                                        profileImageUrl,
                                        opponentName,
                                        reservation.getStatus());
                            }

                            return new ChatRoomPreviewResponse(room.getId(),
                                    profileImageUrl,
                                    opponentName,
                                    reservation.getStatus(),
                                    lastMessage.getContent(),
                                    lastMessage.getCreatedAt()
                            );
                        }
                ).toList();
    }

    private String getOpponentName(Long memberId, ChatRoom room, Map<Long, String> names) {
        return Optional.ofNullable(names.get(room.getOpponentIdOf(memberId)))
                .orElseThrow(() -> new DataIntegrityException(
                        BusinessErrorMessage.CHAT_ROOM_OPPONENT_NAME_INTEGRITY_EXCEPTION.getMessage()));
    }

    private Reservation getReservation(ChatRoom room, Map<Long, Reservation> reservationsById) {
        return Optional.ofNullable(reservationsById.get(room.getReservationId()))
                .orElseThrow(() -> new DataIntegrityException(
                        BusinessErrorMessage.CHAT_ROOM_RESERVATION_INTEGRITY_EXCEPTION.getMessage()));
    }
}
