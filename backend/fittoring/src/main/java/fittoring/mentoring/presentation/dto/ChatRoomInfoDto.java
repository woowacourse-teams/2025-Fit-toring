package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.ChatStatus;
import fittoring.mentoring.business.model.MemberRole;

public record ChatRoomInfoDto(
        Long mentoringId,
        MemberRole myRole,
        String opponentName,
        ChatStatus status
) {
}
