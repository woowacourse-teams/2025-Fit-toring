package fittoring.application.notification.repository;

import fittoring.domain.model.Device;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface DeviceRepository extends ListCrudRepository<Device, Long> {

    Optional<Device> findByMemberId(Long memberId);
}
