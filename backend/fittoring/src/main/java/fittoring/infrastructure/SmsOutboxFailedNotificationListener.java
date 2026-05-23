package fittoring.infrastructure;

import fittoring.infrastructure.discord.DiscordWebhookClient;
import fittoring.infrastructure.event.SmsOutboxFailedEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsOutboxFailedNotificationListener {

    private final DiscordWebhookClient discordWebhookClient;
    private final SmsOutboxResultApplier resultApplier;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFailed(SmsOutboxFailedEvent event) {
        try {
            discordWebhookClient.send(formatMessage(event));
            resultApplier.markFailedNotified(event.outboxId(), LocalDateTime.now());
        } catch (Exception e) {
            log.warn(
                    "Discord 알림 발송 실패: outboxId={}, reservationId={}, attempts={}",
                    event.outboxId(),
                    event.reservationId(),
                    event.attempts(),
                    e
            );
        }
    }

    private String formatMessage(SmsOutboxFailedEvent event) {
        return String.format(
                "[SMS Outbox FAILED] outboxId=%d, reservationId=%d, eventType=%s, toPhone=%s, attempts=%d, lastError=%s",
                event.outboxId(),
                event.reservationId(),
                event.eventType(),
                maskPhone(event.toPhone()),
                event.attempts(),
                event.lastError()
        );
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return phone.substring(0, phone.length() - 4) + "****";
    }
}
