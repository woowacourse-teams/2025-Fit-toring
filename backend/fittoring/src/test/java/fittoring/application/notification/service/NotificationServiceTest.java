package fittoring.application.notification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicateDeviceException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.TooManyDeviceException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Member;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class NotificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoBean
    private NotificationSender notificationSender;

    @DisplayName("멤버 ID와 푸시 토큰으로 디바이스를 등록할 수 있다.")
    @Test
    void registerDevice1() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
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
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String originalToken = "testFcmTokentestFcmTokentestFcmToken";

        notificationService.registerDevice(member.getId(), originalToken);

        // when & then
        Assertions.assertThatThrownBy(
                        () -> notificationService.registerDevice(member.getId(), originalToken))
                .isInstanceOf(DuplicateDeviceException.class);
    }

    @DisplayName("유저는 최대 5개까지 기기를 등록할 수 있다.")
    @Test
    void registerDevice3() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        List<String> pushTokens = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            pushTokens.add("deviceTokenValue" + i);
        }

        // when & then
        SoftAssertions.assertSoftly(softAssertions -> {
            for (int i = 0; i < 6; i++) {
                String pushToken = pushTokens.get(i);
                if (i < 5) {
                    softAssertions.assertThatCode(() -> notificationService.registerDevice(member.getId(), pushToken))
                            .doesNotThrowAnyException();
                } else {
                    softAssertions.assertThatThrownBy(
                                    () -> notificationService.registerDevice(member.getId(), pushToken))
                            .isInstanceOf(TooManyDeviceException.class);
                }
            }
        });
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
