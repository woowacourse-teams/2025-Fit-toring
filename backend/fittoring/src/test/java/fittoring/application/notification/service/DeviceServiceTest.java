package fittoring.application.notification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeviceServiceTest extends IntegrationTestSupport {

    @Autowired
    private FcmService fcmService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("새 디바이스로 FCM 토큰 업서트 요청 시 유효 토큰을 가진 디바이스를 새로 생성한다.")
    @Test
    void upsertFcmToken1() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String token = "testFcmTokentestFcmTokentestFcmToken";

        // when
        fcmService.upsertFcmToken(member.getId(), token);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(deviceRepository.findByMemberId(member.getId())).isPresent();
            softAssertions.assertThat(deviceRepository.findByMemberId(member.getId()).get().getToken())
                    .isEqualTo(token);
        });
    }

    @DisplayName("기존 디바이스에 FCM 토큰 업서트 요청 시 토큰 값이 갱신된다.")
    @Test
    void upsertFcmToken2() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String originalToken = "testFcmTokentestFcmTokentestFcmToken";
        String newToken = "newTestFcmTokennewTestFcmTokennewTestFcmToken";
        fcmService.upsertFcmToken(member.getId(), originalToken);

        // when
        fcmService.upsertFcmToken(member.getId(), newToken);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(deviceRepository.findByMemberId(member.getId())).isPresent();
            softAssertions.assertThat(deviceRepository.findByMemberId(member.getId()).get().getToken())
                    .isEqualTo(newToken);
        });
    }

    @DisplayName("존재하지 않는 유저가 FCM 토큰 업서트 요청 시 예외가 발생한다.")
    @Test
    void upsertFcmTokenFail() {
        // given
        String token = "testFcmTokentestFcmTokentestFcmToken";

        // when & then
        assertThatThrownBy(() -> fcmService.upsertFcmToken(999L, token))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }
}
