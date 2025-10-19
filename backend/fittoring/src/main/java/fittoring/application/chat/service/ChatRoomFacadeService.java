package fittoring.application.chat.service;

import fittoring.application.chat.service.dto.ChatRoomInfoDto;
import fittoring.application.chat.service.dto.ChatRoomInfoResponse;
import fittoring.application.mentoring.service.MentoringService;
import fittoring.application.mentoring.service.dto.ChatRoomMentoringInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomFacadeService {

    private final MentoringService mentoringService;
    private final ChatRoomService chatRoomService;

    public ChatRoomInfoResponse getChatRoom(Long chatroomId, Long memberId) {
        ChatRoomInfoDto chatRoomInfo = chatRoomService.findChatRoom(chatroomId, memberId);
        ChatRoomMentoringInfoDto mentoringInfo = mentoringService.findMentoringInfoForChatRoom(
                chatRoomInfo.mentoringId()
        );
        return ChatRoomInfoResponse.of(mentoringInfo, chatRoomInfo);
    }
}
