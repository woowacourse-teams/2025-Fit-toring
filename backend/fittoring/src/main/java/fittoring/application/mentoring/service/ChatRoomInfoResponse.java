package fittoring.application.mentoring.service;

import fittoring.application.chatroom.service.dto.ChatRoomInfoDto;
import fittoring.application.mentoring.service.dto.chat.ChatRoomMentoringInfoDto;

public record ChatRoomInfoResponse(
        ChatRoomMentoringInfoDto mentoringInfoDto,
        ChatRoomInfoDto chatRoomInfoDto
) {
}
