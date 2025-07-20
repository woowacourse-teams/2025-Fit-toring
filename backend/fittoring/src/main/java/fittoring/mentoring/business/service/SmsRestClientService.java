package fittoring.mentoring.business.service;

import fittoring.mentoring.business.service.dto.SmsSendClientDto;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class SmsRestClientService {

    private static final String RESERVATION_SUBJECT = "핏토링 예약 알림";
    private static final String SEND_MESSAGE_ENDPOINT = "/messages/v4/send-many/detail";

    private final RestClient smsRestClient;
    private final SmsAuthHeaderGenerator authHeaderGenerator;

    @Value("${SMS_FROM_PHONE}")
    private String fromPhone;

    public void sendSms(String to, String text) throws NoSuchAlgorithmException, InvalidKeyException {
        String body = smsRestClient.post()
                .uri(SEND_MESSAGE_ENDPOINT)
                .header("Authorization", authHeaderGenerator.createAuthorization())
                .body(Map.of("messages", List.of(new SmsSendClientDto(
                        to,
                        fromPhone,
                        text,
                        RESERVATION_SUBJECT
                ))))
                .retrieve()
                .body(String.class);
    }
}
