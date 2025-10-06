package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.ChatRoom;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.repository.ChatRoomRepository;
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
                reservation.getMentoring().getMentor().getId()
        );
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        String url = chatRoomUrlGenerator.generate(savedChatRoom.getId());
        return new ChatRoomCreatedInfo(url);
    }
}
