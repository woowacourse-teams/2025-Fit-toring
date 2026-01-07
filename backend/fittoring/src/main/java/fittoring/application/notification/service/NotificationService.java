package fittoring.application.notification.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.TooManyDeviceException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Device;
import fittoring.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    public static final int DEVICE_LIMIT = 5;

    private final NotificationSender notificationSender;
    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void upsertPushToken(Long memberId, String hardwareId, String pushToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));

        deviceRepository.findByMemberAndHardwareId(member, hardwareId)
                .ifPresentOrElse(
                        device -> device.updateToken(pushToken),
                        () -> registerNewDevice(member, hardwareId, pushToken)
                );
    }

    private void registerNewDevice(Member member, String hardwareId, String pushToken) {
        Device device = new Device(member, hardwareId, pushToken);
        deviceRepository.save(device);
    }

    public void notifyNewMessage(Long memberId) {
        List<Device> devices = deviceRepository.findAllByMemberId(memberId);
        validateDeviceCount(devices);
        notificationSender.send(devices, "핏토링", "채팅이 도착하였습니다.");
    }

    private void validateDeviceCount(List<Device> devices) {
        if (devices.size() > DEVICE_LIMIT) {
            throw new TooManyDeviceException(BusinessErrorMessage.TOO_MANY_DEVICE.getMessage());
        }
    }
}
