package fittoring.mentoring.business.service;

import fittoring.mentoring.business.service.dto.chat.ChatRoomMentoringInfoDto;
import fittoring.mentoring.presentation.dto.ChatRoomInfoDto;

public record ChatRoomInfoResponse(
        ChatRoomMentoringInfoDto mentoringInfoDto,
        ChatRoomInfoDto chatRoomInfoDto
) {
}
