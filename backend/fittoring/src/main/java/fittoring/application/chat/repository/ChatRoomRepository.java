package fittoring.application.chat.repository;

import fittoring.domain.model.ChatRoom;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface ChatRoomRepository extends ListCrudRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByReservationId(Long reservationId);

    boolean existsByReservationId(Long reservationId);
}
