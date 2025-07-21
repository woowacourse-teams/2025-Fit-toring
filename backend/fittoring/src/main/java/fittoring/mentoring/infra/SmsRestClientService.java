package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.dto.SmsSendClientDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    public void sendSms(String to, String subject, String text) {
        smsRestClient.post()
                .uri(SEND_MESSAGE_ENDPOINT)
                .header("Authorization", authHeaderGenerator.createAuthorization())
                .body(Map.of("messages", List.of(new SmsSendClientDto(
                        to,
                        fromPhone,
                        text,
                        subject
                ))))
                .retrieve()
                .body(String.class);
    }
}
