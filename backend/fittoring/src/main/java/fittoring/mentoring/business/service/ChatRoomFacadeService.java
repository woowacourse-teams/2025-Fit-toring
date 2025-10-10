package fittoring.mentoring.business.service;

import fittoring.mentoring.business.service.dto.chat.ChatRoomMentoringInfoDto;
import fittoring.mentoring.presentation.dto.ChatRoomInfoDto;
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
