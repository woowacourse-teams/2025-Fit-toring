package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.ChatMessage;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends ListCrudRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomId(Long chatroomId);
}
