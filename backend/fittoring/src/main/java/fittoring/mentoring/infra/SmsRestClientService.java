package fittoring.mentoring.infra;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.service.dto.LongSmsSendClientDto;
import fittoring.mentoring.business.service.dto.ShortSmsSendClientDto;
import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.SmsException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class SmsRestClientService {

    private static final String SEND_MESSAGE_ENDPOINT = "/messages/v4/send-many/detail";

    private final RestClient smsRestClient;
    private final SmsAuthHeaderGenerator authHeaderGenerator;

    @Value("${COOL_SMS_FROM_PHONE}")
    private String fromPhone;

    public void sendSms(String toPhone, String text, String subject) {
        smsRestClient.post()
                .uri(SEND_MESSAGE_ENDPOINT)
                .header("Authorization", authHeaderGenerator.createAuthorization())
                .body(Map.of("messages", List.of(new LongSmsSendClientDto(
                        toPhone,
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
