package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.ChatRoom;
import org.springframework.data.repository.ListCrudRepository;

public interface ChatRoomRepository extends ListCrudRepository<ChatRoom, Long> {
}
