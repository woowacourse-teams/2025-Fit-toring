package fittoring.application.chat.repository;

import fittoring.domain.model.ChatRoom;
import org.springframework.data.repository.ListCrudRepository;

public interface ChatRoomRepository extends ListCrudRepository<ChatRoom, Long> {

    boolean existsByReservationId(Long reservationId);
}
