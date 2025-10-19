package fittoring.application.chat.repository;

import fittoring.domain.model.ChatMessage;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends ListCrudRepository<ChatMessage, Long>, CustomChatMessageRepository {
}
