package fittoring.infrastructure;

import fittoring.domain.model.Phone;
import fittoring.infrastructure.dto.BatchSendRequestEntry;
import fittoring.infrastructure.dto.BatchSendResponseDto;
import fittoring.infrastructure.dto.BatchSendResult;
import fittoring.infrastructure.dto.LongSmsSendClientDto;
import fittoring.infrastructure.dto.ShortSmsSendClientDto;
import fittoring.infrastructure.dto.SmsOutboxMessage;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.SmsException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SmsRestClientService {

    private static final String SEND_MESSAGE_ENDPOINT = "/messages/v4/send-many/detail";

    private final RestClient smsRestClient;

    private final SmsAuthHeaderGenerator authHeaderGenerator;

    @Value("${COOL_SMS_FROM_PHONE}")
    private String fromPhone;

    public SmsRestClientService(@Qualifier("smsRestClient") RestClient smsRestClient,
                                SmsAuthHeaderGenerator authHeaderGenerator) {
        this.smsRestClient = smsRestClient;
        this.authHeaderGenerator = authHeaderGenerator;
    }

    public void sendSms(Phone toPhone, String text, String subject) {
        smsRestClient.post()
                .uri(SEND_MESSAGE_ENDPOINT)
                .header("Authorization", authHeaderGenerator.createAuthorization())
                .body(Map.of("messages", List.of(new LongSmsSendClientDto(
                        toPhone.getNumber(),
                        fromPhone,
                        text,
                        subject
                ))))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new SmsException(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
                })
                .body(String.class);
    }

    public static final String OUTBOX_ID_CUSTOM_FIELD = "outboxId";

    public BatchSendResult sendBatch(List<SmsOutboxMessage> messages) {
        List<BatchSendRequestEntry> payload = messages.stream()
                .map(message -> new BatchSendRequestEntry(
                        message.to().getNumber(),
                        fromPhone,
                        message.text(),
                        message.subject(),
                        Map.of(OUTBOX_ID_CUSTOM_FIELD, String.valueOf(message.outboxId()))
                ))
                .toList();

        BatchSendResponseDto response = smsRestClient.post()
                .uri(SEND_MESSAGE_ENDPOINT)
                .header("Authorization", authHeaderGenerator.createAuthorization())
                .body(Map.of("messages", payload))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                    throw new SmsException(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, clientResponse) -> {
                    throw new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
                })
                .body(BatchSendResponseDto.class);

        List<BatchSendResponseDto.FailedMessageDto> failures =
                response == null || response.failedMessageList() == null
                        ? Collections.emptyList()
                        : response.failedMessageList();
        Set<Long> failedOutboxIds = failures.stream()
                .map(this::extractOutboxId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return BatchSendResult.of(failedOutboxIds);
    }

    private Long extractOutboxId(BatchSendResponseDto.FailedMessageDto failure) {
        if (failure.customFields() == null) {
            return null;
        }
        String raw = failure.customFields().get(OUTBOX_ID_CUSTOM_FIELD);
        if (raw == null) {
            return null;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public void sendSms(Phone toPhone, String text) {
        smsRestClient.post()
                .uri(SEND_MESSAGE_ENDPOINT)
                .header("Authorization", authHeaderGenerator.createAuthorization())
                .body(Map.of("messages", List.of(new ShortSmsSendClientDto(
                        toPhone.getNumber(),
                        fromPhone,
                        text
                ))))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new SmsException(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
                })
                .body(String.class);
    }
}
