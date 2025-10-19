package fittoring.application.chatroom.service.dto;

import fittoring.application.mentoring.service.dto.ChatRoomMentoringInfoDto;
import fittoring.domain.model.ChatStatus;
import fittoring.domain.model.MemberRole;

public record ChatRoomInfoResponse(
        String mentorName,
        int price,
        String profileImageUrl,
        Long mentoringId,
        MemberRole myRole,
        String opponentName,
        ChatStatus status
) {

    public static ChatRoomInfoResponse of(
            ChatRoomMentoringInfoDto chatRoomMentoringInfoDto,
            ChatRoomInfoDto chatRoomInfoDto
    ) {
        return new ChatRoomInfoResponse(
                chatRoomMentoringInfoDto.mentorName(),
                chatRoomMentoringInfoDto.price(),
                chatRoomMentoringInfoDto.profileImageUrl(),
                chatRoomInfoDto.mentoringId(),
                chatRoomInfoDto.myRole(),
                chatRoomInfoDto.opponentName(),
                chatRoomInfoDto.status()
        );
    }
}
