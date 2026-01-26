package fittoring.application.notification.service;

import fittoring.domain.model.Device;
import fittoring.domain.model.Notification;
import java.util.List;

public interface NotificationSender {

    void send(List<Device> devices, Notification notification);
}
