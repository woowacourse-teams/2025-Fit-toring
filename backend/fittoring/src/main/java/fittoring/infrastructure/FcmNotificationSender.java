package fittoring.infrastructure;

import fittoring.application.notification.service.NotificationSender;
import fittoring.domain.model.Device;
import fittoring.domain.model.Notification;
import fittoring.infrastructure.dto.FcmNotificationRequest;
import fittoring.infrastructure.exception.InfraErrorMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Profile({"!local & !test"})
public class FcmNotificationSender implements NotificationSender {

    public static final int PUSH_REQUEST_BATCH_UNIT = 10;
    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.push-notification-queue}")
    private String queueName;

    /**
     * 지정된 Device들에게 푸시 알림 전송 메세지를 SQS로 보냅니다. 특정 단위로 메세지를 배치 처리합니다. 비동기로 동작합니다.
     *
     * @param devices      푸시 알림을 보낼 Device 목록
     * @param notification 푸시 알림 데이터
     */
    @Override
    public void send(List<Device> devices, Notification notification) {
        log.info("알림 Device 수: {} 개", devices.size());

        Map<String, String> map = notification.getData();
        List<Message<FcmNotificationRequest>> messages = devices.stream()
                .filter(Device::isPushEnabled)
                .map(d -> MessageBuilder.withPayload(new FcmNotificationRequest(d.getPushToken(), map)).build())
                .toList();

        for (int i = 0; i < messages.size(); i += PUSH_REQUEST_BATCH_UNIT) {
            List<Message<FcmNotificationRequest>> batch = messages.subList(i,
                    Math.min(i + PUSH_REQUEST_BATCH_UNIT, messages.size()));
            sqsTemplate.sendManyAsync(queueName, batch)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.warn(InfraErrorMessage.SEND_SQS_ERROR.getMessage(), exception);
                        }
                    });
        }
    }
}
