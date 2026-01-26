package fittoring.infrastructure;

import fittoring.application.notification.service.NotificationService;
import fittoring.infrastructure.dto.UnRegisteredFcmTokenDeleteRequest;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UnRegisteredFcmTokenListener {

    private final NotificationService notificationService;

    @SqsListener("${aws.sqs.push-notification-unregistered-tokens-queue}")
    public void handle(UnRegisteredFcmTokenDeleteRequest request) {
        notificationService.deleteDeviceByPushToken(request.fcmToken());
    }
}
