package fittoring.infrastructure;

import fittoring.application.notification.service.NotificationSender;
import fittoring.domain.model.Device;
import fittoring.domain.model.Notification;
import fittoring.infrastructure.dto.FcmNotificationRequest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmNotificationSender implements NotificationSender {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.push-notification-queue}")
    private String queueName;

    @Override
    public void send(List<Device> devices, Notification notification) {
        log.info("알림 Device 수: {} 개", devices.size());
        devices.stream()
                .filter(Device::isPushEnabled)
                .forEach(device -> {
                            sqsTemplate.send(to -> to.queue(queueName)
                                    .payload(new FcmNotificationRequest(device.getPushToken(), notification.getData())));
                        }
                );
    }
}
