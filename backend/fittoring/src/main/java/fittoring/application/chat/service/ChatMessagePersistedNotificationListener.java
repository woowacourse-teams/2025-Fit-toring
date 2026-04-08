package fittoring.application.chat.service;

import fittoring.application.chat.service.event.ChatMessagePersistedEvent;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.service.NotificationService;
import fittoring.domain.model.ChatMessageType;
import fittoring.domain.model.Notification;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class ChatMessagePersistedNotificationListener {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final Timer chatPersistNotificationTimer;

    public ChatMessagePersistedNotificationListener(
            NotificationService notificationService,
            MemberRepository memberRepository,
            MeterRegistry meterRegistry
    ) {
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
        this.chatPersistNotificationTimer = Timer.builder("chat_persist_notification_seconds")
                .description("Latency of notification preparation and dispatch in chat persistence flow")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationAfterCommit(ChatMessagePersistedEvent event) {
        Timer.Sample notificationSample = Timer.start();
        try {
            String senderName = memberRepository.findNameById(event.senderId())
                    .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));

            Notification notification = new Notification(senderName, event.chatMessage().getContent());
            if (event.chatMessage().getMessageType() == ChatMessageType.IMAGE) {
                notification.setImageNotificationBody();
            }
            notification.putData("chatRoomId", String.valueOf(event.chatRoomId()));
            notificationService.sendNotification(event.opponentId(), notification);
        } catch (Exception e) {
            log.error("채팅 알림 전송에 실패했습니다. messageId={}", event.chatMessage().getMessageId(), e);
        } finally {
            notificationSample.stop(chatPersistNotificationTimer);
        }
    }
}
