package fittoring.infrastructure;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import fittoring.application.notification.service.NotificationSender;
import fittoring.domain.model.Device;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmNotificationSender implements NotificationSender {

    @Override
    public List<Device> send(List<Device> devices, String title, String body) {
        List<Device> failedDevies = new ArrayList<>();
        for (Device device : devices) {
            if (device.isPushEnabled()) {
                try {
                    sendNotification(device.getPushToken(), title, body);
                } catch (FirebaseMessagingException exception) {
                    if (exception.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                        failedDevies.add(device);
                    }
                }
            }
        }
        return failedDevies;
    }

    private void sendNotification(String fcmToken, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        FirebaseMessaging.getInstance().send(message);
    }
}
