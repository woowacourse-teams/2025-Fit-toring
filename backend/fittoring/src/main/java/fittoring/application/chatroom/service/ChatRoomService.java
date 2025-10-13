package fittoring.application.chatroom.service;

import fittoring.application.mentoring.repository.ChatRoomRepository;
import fittoring.application.mentoring.service.ChatRoomUrlGenerator;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Reservation;
import fittoring.mentoring.business.service.dto.chat.ChatRoomCreatedInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomUrlGenerator chatRoomUrlGenerator;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoomCreatedInfo registerChatRoom(Reservation reservation) {
        ChatRoom chatRoom = new ChatRoom(
                reservation.getId(),
                reservation.getMentee().getId(),
                reservation.getMentor().getId()
        );
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        String url = chatRoomUrlGenerator.generate(savedChatRoom.getId());
        return new ChatRoomCreatedInfo(url);
    }
}
