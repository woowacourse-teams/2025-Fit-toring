package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.ChatMessage;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends ListCrudRepository<ChatMessage, Long>, CustomChatMessageRepository {
}
