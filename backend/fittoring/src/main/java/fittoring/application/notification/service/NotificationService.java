package fittoring.application.notification.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicateDeviceException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.TooManyDeviceException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Device;
import fittoring.domain.model.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationService {

    public static final int DEVICE_LIMIT = 5;

    private final NotificationSender notificationSender;
    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void registerDevice(Long memberId, String pushToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        validateAlreadyRegistered(member, pushToken);
        List<Device> devices = deviceRepository.findAllByMemberId(memberId);
        validateDeviceCount(devices);
        deviceRepository.save(new Device(member, pushToken));
    }

    private void validateAlreadyRegistered(Member member, String pushToken) {
        if (deviceRepository.existsByMemberAndPushToken(member, pushToken)) {
            throw new DuplicateDeviceException(BusinessErrorMessage.ALREADY_REGISTERED_DEVICE.getMessage());
        }
    }

    public void notifyNewMessage(Long memberId) {
        List<Device> devices = deviceRepository.findAllByMemberId(memberId);
        notificationSender.send(devices, "핏토링", "채팅이 도착하였습니다.");
    }

    private void validateDeviceCount(List<Device> devices) {
        if (devices.size() >= DEVICE_LIMIT) {
            throw new TooManyDeviceException(BusinessErrorMessage.TOO_MANY_DEVICE.getMessage());
        }
    }
}
