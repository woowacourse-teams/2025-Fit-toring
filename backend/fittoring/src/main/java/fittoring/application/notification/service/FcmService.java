package fittoring.application.notification.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Device;
import fittoring.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FcmService {

    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void upsertFcmToken(Long memberId, String hardwareId, String pushToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));

        deviceRepository.findByMemberAndHardwareId(member, hardwareId).ifPresentOrElse(
                device -> device.updateToken(pushToken),
                () -> registerNewDevice(member, hardwareId, pushToken)
        );
    }

    private void registerNewDevice(Member member, String hardwareId, String pushToken) {
        Device device = new Device(member, hardwareId, pushToken);
        deviceRepository.save(device);
    }
}
