package fittoring.application.chatroom.repository;

import fittoring.application.mentoring.repository.CustomChatMessageRepository;
import fittoring.domain.model.ChatMessage;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends ListCrudRepository<ChatMessage, Long>, CustomChatMessageRepository {
}
