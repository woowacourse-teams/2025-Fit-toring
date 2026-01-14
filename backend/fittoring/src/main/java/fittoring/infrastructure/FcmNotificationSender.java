package fittoring.infrastructure;

import com.google.firebase.messaging.*;
import fittoring.application.notification.service.NotificationSender;
import fittoring.domain.model.Device;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmNotificationSender implements NotificationSender {

    @Override
    public List<Device> send(List<Device> devices, String title, String body) {
        List<Device> failedDevices = new ArrayList<>();
        log.info("알림 Device 수: {} 개", devices.size());
        for (Device device : devices) {
            if (device.isPushEnabled()) {
                try {
                    sendNotification(device.getPushToken(), title, body);
                } catch (FirebaseMessagingException exception) {
                    log.error("알림 전송 실패 -> PushToken: {}, MessagingErrorCode: {}, Message: {}", device.getPushToken(), exception.getMessagingErrorCode(), exception.getMessage());
                    if (exception.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                        failedDevices.add(device);
                    }
                }
            }
        }
        return failedDevices;
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
