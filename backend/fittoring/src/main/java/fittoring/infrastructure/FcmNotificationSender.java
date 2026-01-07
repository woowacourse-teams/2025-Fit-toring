package fittoring.infrastructure;

import com.google.firebase.messaging.*;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.TooManyDeviceException;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.application.notification.service.NotificationSender;
import fittoring.domain.model.Device;
import fittoring.infrastructure.exception.FcmSendException;
import fittoring.infrastructure.exception.InfraErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FcmNotificationSender implements NotificationSender {

    private final DeviceRepository deviceRepository;

    @Override
    public void send(List<Device> devices, String title, String body) {
        for (Device device : devices) {
            if (device.isPushEnabled()) {
                sendNotification(device.getPushToken(), title, body);
            }
        }
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
