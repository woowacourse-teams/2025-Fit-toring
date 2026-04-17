package fittoring.infrastructure;

import fittoring.application.notification.service.NotificationService;
import fittoring.infrastructure.dto.UnRegisteredFcmTokenDeleteRequest;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"!local & !test"})
public class UnRegisteredFcmTokenListener {

    private final NotificationService notificationService;

    @SqsListener("${aws.sqs.push-notification-unregistered-tokens-queue}")
    public void handle(UnRegisteredFcmTokenDeleteRequest request) {
        log.error("미등록 토큰 삭제: {}", request.fcmToken());
        notificationService.deleteDeviceByPushToken(request.fcmToken());
    }
}
