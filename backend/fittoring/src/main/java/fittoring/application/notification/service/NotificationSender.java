package fittoring.application.notification.service;

import fittoring.domain.model.Device;

import java.util.List;

public interface NotificationSender {

    void send(List<Device> devices, String title, String body);
}
