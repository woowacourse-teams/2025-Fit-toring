package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.response.ChatRoomPreviewResponse;
import fittoring.application.chat.service.dto.ChatRoomInfoDto;
import fittoring.application.chat.presentation.dto.response.ChatRoomInfoResponse;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.service.MemberService;
import fittoring.application.mentoring.service.MentoringService;
import fittoring.application.mentoring.service.dto.ChatRoomMentoringInfoDto;
import fittoring.application.reservation.service.ReservationService;
import fittoring.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<ChatRoomPreviewResponse> getChatRoomPreviews(Long memberId) {
        List<ChatRoom> chatRooms = chatRoomService.findAllByMemberId(memberId);
        if (chatRooms.isEmpty()) {
            return List.of();
        }
        /*
         * N+1문제 방지를 위해 채팅방, 메시지, 예약, 멘토링, 회원, 이미지의 연관된 데이터를
         * 각각 배치 조회하고, Map 으로 연결하여 조립합니다.
         */
        // 마지막 메시지 배치 조회 및 매핑
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getId)
                .toList();
        Map<Long, ChatMessage> chatRoomToLastMessage = chatMessageService.getChatRoomToLastChatMessageByChatRoomIds(chatRoomIds);

        // 예약 배치 조회 및 매핑, N+1 방지를 위해 멘토링 Fetch Join
        List<Long> reservationIds = chatRooms.stream()
                .map(ChatRoom::getReservationId)
                .toList();
        List<Reservation> reservations = reservationService.findAllByIdsWithMentoring(reservationIds);
        Map<Long, Reservation> reservationMap = reservations.stream()
                .collect(Collectors.toMap(Reservation::getId, Function.identity()));

        // 상대방 이름 배치 조회 및 매핑
        List<Long> opponentsIds = chatRooms.stream()
                .map(chatRoom -> chatRoom.getOpponentIdOf(memberId))
                .toList();
        Map<Long, String> names = memberService.getIdNameMap(opponentsIds);

        // 프로필 이미지 배치 조회 및 매핑
        List<Long> mentoringIds = reservations.stream()
                .map(Reservation::getMentoring)
                .map(Mentoring::getId)
                .toList();
        Map<Long, String> mentoringIdToProfileImageUrl = imageService.findThumbnailImageMapByImageTypeAndRelationIds(ImageType.MENTORING_PROFILE, mentoringIds);

        return chatRooms.stream()
                .map(
                        room -> {
                            Reservation reservation = reservationMap.get(room.getReservationId());
                            Long mentoringId = reservation.getMentoring().getId();
                            String opponentName = names.get(room.getOpponentIdOf(memberId));
                            String profileImageUrl = mentoringIdToProfileImageUrl.get(mentoringId);
                            ChatMessage lastMessage = chatRoomToLastMessage.get(room.getId());

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
}
