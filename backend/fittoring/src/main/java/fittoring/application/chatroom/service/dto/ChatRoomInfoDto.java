package fittoring.application.chatroom.service.dto;

import fittoring.domain.model.ChatStatus;
import fittoring.domain.model.MemberRole;

public record ChatRoomInfoDto(
        Long mentoringId,
        MemberRole myRole,
        String opponentName,
        ChatStatus status
) {
}
