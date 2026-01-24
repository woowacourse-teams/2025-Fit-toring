package fittoring.application.notification.repository;

import fittoring.admin.repository.CustomDeviceRepository;
import fittoring.domain.model.Device;
import fittoring.domain.model.Member;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

public interface DeviceRepository extends ListCrudRepository<Device, Long>, CustomDeviceRepository {

    List<Device> findAllByMemberId(Long memberId);

    boolean existsByMemberAndPushToken(Member member, String pushToken);
}
