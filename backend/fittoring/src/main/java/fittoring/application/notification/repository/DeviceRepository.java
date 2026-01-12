package fittoring.application.notification.repository;

import fittoring.domain.model.Device;

import java.util.List;
import java.util.Optional;

import fittoring.domain.model.Member;
import org.springframework.data.repository.ListCrudRepository;

public interface DeviceRepository extends ListCrudRepository<Device, Long> {

    Optional<Device> findByMemberAndPushToken(Member member, String pushToken);

    List<Device> findAllByMemberId(Long memberId);

    boolean existsByMemberAndPushToken(Member member, String pushToken);
}
