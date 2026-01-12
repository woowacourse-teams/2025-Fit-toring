package fittoring.application.notification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicateDeviceException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Member;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("멤버 ID와 푸시 토큰으로 디바이스를 등록할 수 있다.")
    @Test
    void registerDevice1() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String pushToken = "testFcmTokentestFcmTokentestFcmToken";

        // when
        notificationService.registerDevice(member.getId(), pushToken);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(deviceRepository.findAllByMemberId(member.getId()).getFirst().getPushToken())
                    .isEqualTo(pushToken);
        });
    }

    @DisplayName("유저가 동일 토큰으로 재등록시 예외가 발생한다.")
    @Test
    void registerDevice2() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String originalToken = "testFcmTokentestFcmTokentestFcmToken";

        notificationService.registerDevice(member.getId(), originalToken);

        // when & then
        Assertions.assertThatThrownBy(
                () -> notificationService.registerDevice(member.getId(), originalToken))
                .isInstanceOf(DuplicateDeviceException.class);
    }

    @DisplayName("존재하지 않는 유저가 디바이스 등록 요청 시 예외가 발생한다.")
    @Test
    void registerDeviceFail() {
        // given
        String token = "testFcmTokentestFcmTokentestFcmToken";

        // when & then
        assertThatThrownBy(() -> notificationService.registerDevice(999L, token))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }
}
