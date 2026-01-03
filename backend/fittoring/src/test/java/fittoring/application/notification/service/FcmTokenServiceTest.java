package fittoring.application.notification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.FcmTokenRepository;
import fittoring.domain.model.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FcmTokenServiceTest extends IntegrationTestSupport {

    @Autowired
    private FcmTokenService fcmTokenService;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("FCM 토큰이 저장되지 않은 유저가 FCM 토큰 업서트 요청 시 새로운 FCM 토큰이 저장된다.")
    @Test
    void upsertFcmToken1() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String token = "testFcmTokentestFcmTokentestFcmToken";

        // when
        fcmTokenService.upsertFcmToken(member.getId(), token);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(fcmTokenRepository.findByMemberId(member.getId())).isPresent();
            softAssertions.assertThat(fcmTokenRepository.findByMemberId(member.getId()).get().getToken())
                    .isEqualTo(token);
        });
    }

    @DisplayName("FCM 토큰이 저장된 유저가 FCM 토큰 업서트 요청 시  FCM 토큰 값이 갱신된다.")
    @Test
    void upsertFcmToken2() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String originalToken = "testFcmTokentestFcmTokentestFcmToken";
        String newToken = "newTestFcmTokennewTestFcmTokennewTestFcmToken";
        fcmTokenService.upsertFcmToken(member.getId(), originalToken);

        // when
        fcmTokenService.upsertFcmToken(member.getId(), newToken);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(fcmTokenRepository.findByMemberId(member.getId())).isPresent();
            softAssertions.assertThat(fcmTokenRepository.findByMemberId(member.getId()).get().getToken())
                    .isEqualTo(newToken);
        });
    }

    @DisplayName("존재하지 않는 유저가 FCM 토큰 업서트 요청 시 예외가 발생한다.")
    @Test
    void upsertFcmTokenFail() {
        // given
        String token = "testFcmTokentestFcmTokentestFcmToken";

        // when & then
        assertThatThrownBy(() -> fcmTokenService.upsertFcmToken(999L, token))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }
}
