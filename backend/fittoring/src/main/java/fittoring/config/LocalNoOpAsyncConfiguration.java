package fittoring.config;

import fittoring.application.chat.service.port.ChatMessagePersistEventPublisher;
import fittoring.application.notification.service.NotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "test"})
public class LocalNoOpAsyncConfiguration {

    @Bean
    public ChatMessagePersistEventPublisher chatMessagePersistEventPublisher() {
        return event -> {
        };
    }

    @Bean
    public NotificationSender notificationSender() {
        return (devices, notification) -> {
        };
    }
}
