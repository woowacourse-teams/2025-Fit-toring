package fittoring.application.chat.repository;

import fittoring.domain.model.ChatRoom;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface ChatRoomRepository extends ListCrudRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByReservationIdIn(List<Long> reservationIds);

    boolean existsByReservationId(Long reservationId);
}
