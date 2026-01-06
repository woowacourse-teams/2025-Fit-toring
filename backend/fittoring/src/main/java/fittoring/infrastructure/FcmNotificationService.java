package fittoring.infrastructure;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.TooManyDeviceException;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.application.notification.service.NotificationService;
import fittoring.domain.model.Device;
import fittoring.infrastructure.exception.FcmSendException;
import fittoring.infrastructure.exception.InfraErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FcmNotificationService implements NotificationService {

    public static final int DEVICE_LIMIT = 5;
    private final DeviceRepository deviceRepository;

    @Override
    public void sendNotification(Long memberId, String title, String body) {
        List<Device> devices = deviceRepository.findAllByMemberId(memberId);
        validateDeviceCount(devices);
        for (Device device : devices) {
            sendNotification(device.getPushToken(), title, body);
        }
    }

    private void validateDeviceCount(List<Device> devices) {
        if (devices.size() > DEVICE_LIMIT) {
            throw new TooManyDeviceException(BusinessErrorMessage.TOO_MANY_DEVICE.getMessage());
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
