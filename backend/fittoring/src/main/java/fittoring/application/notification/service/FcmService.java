package fittoring.application.notification.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Device;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FcmService {

    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void upsertFcmToken(Long memberId, String token) {
        // TODO: 중복해서 나올 수 있음. 여러 대에 동시에 보낼 것인가?
        Optional<Device> deviceOptional = deviceRepository.findByMemberId(memberId);
        if (deviceOptional.isEmpty()) {
            registerNewDevice(memberId, token);
            return;
        }
        renewFcmToken(deviceOptional.get(), token);
    }

    private void registerNewDevice(Long memberId, String token) {
        validateMemberExists(memberId);
        Device device = new Device(memberId, token, true);
        deviceRepository.save(device);
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
        }
    }

    private void renewFcmToken(Device device, String newToken) {
        device.updateToken(newToken);
        deviceRepository.save(device);
    }
}
