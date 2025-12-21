package fittoring.application.notification.service;

public interface NotificationService {

    void sendNotification(Long memberId, String title, String body);
}
