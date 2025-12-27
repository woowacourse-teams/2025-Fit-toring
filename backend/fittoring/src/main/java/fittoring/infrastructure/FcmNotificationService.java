package fittoring.infrastructure;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberFcmTokenNotFoundException;
import fittoring.application.notification.repository.MemberFcmTokenRepository;
import fittoring.application.notification.service.NotificationService;
import fittoring.domain.model.MemberFcmToken;
import fittoring.infrastructure.dto.FcmNotificationRequest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmNotificationService implements NotificationService {

    private final MemberFcmTokenRepository memberFcmTokenRepository;
    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.push-notification-queue}")
    private String queueName;

    @Override
    public void sendNotification(Long memberId, String title, String body) {
        MemberFcmToken memberFcmToken = memberFcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberFcmTokenNotFoundException(
                        BusinessErrorMessage.MEMBER_FCM_TOKEN_NOT_FOUND.getMessage()));

        FcmNotificationRequest request = new FcmNotificationRequest(
                memberFcmToken.getToken(),
                title,
                body,
                memberId
        );

        sqsTemplate.send(to -> to.queue(queueName).payload(request));
    }
}
