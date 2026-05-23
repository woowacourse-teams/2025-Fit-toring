package fittoring.infrastructure.discord;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class DiscordWebhookClient {

    private final RestClient restClient;
    private final String webhookUrl;

    public DiscordWebhookClient(
            @Qualifier("defaultRestClient") RestClient restClient,
            @Value("${discord.sms-outbox.webhook-url:}") String webhookUrl
    ) {
        this.restClient = restClient;
        this.webhookUrl = webhookUrl;
    }

    public void send(String content) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.debug("Discord webhook URL이 비어 있어 알림을 건너뜁니다.");
            return;
        }
        restClient.post()
                .uri(webhookUrl)
                .body(Map.of("content", content))
                .retrieve()
                .toBodilessEntity();
    }
}
