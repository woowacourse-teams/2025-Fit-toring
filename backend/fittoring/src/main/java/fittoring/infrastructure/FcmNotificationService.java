package fittoring.infrastructure;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberFcmTokenNotFoundException;
import fittoring.application.notification.repository.MemberFcmTokenRepository;
import fittoring.application.notification.service.NotificationService;
import fittoring.domain.model.MemberFcmToken;
import fittoring.infrastructure.exception.FcmSendException;
import fittoring.infrastructure.exception.InfraErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmNotificationService implements NotificationService {

    private final MemberFcmTokenRepository memberFcmTokenRepository;

    @Override
    public void sendNotification(Long memberId, String title, String body) {
        MemberFcmToken memberFcmToken = memberFcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberFcmTokenNotFoundException(
                        BusinessErrorMessage.MEMBER_FCM_TOKEN_NOT_FOUND.getMessage()));
        sendNotification(memberFcmToken.getToken(), title, body);
    }

    private void sendNotification(String fcmToken, String title, String body) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException exception) {
            throw new FcmSendException(InfraErrorMessage.FCM_SEND_ERROR + exception.getMessage());
        }
    }
}
