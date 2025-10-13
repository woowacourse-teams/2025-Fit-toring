package fittoring.application.mentoring.service;

import fittoring.application.chatroom.service.ChatRoomService;
import fittoring.application.chatroom.service.dto.ChatRoomInfoDto;
import fittoring.application.mentoring.service.dto.chat.ChatRoomMentoringInfoDto;
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
        return new ChatRoomInfoResponse(mentoringInfo, chatRoomInfo);
    }
}
