package fittoring.mentoring.business.service;

import fittoring.config.SmsRestClientConfiguration;
import fittoring.mentoring.business.service.dto.SmsSendClientDto;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class SmsRestClientService {

    public static final String FROM_PHONE = "010-4736-7769";
    public static final String RESERVATION_SUBJECT = "핏토링 예약 알림";

    private final RestClient restClient;
    private final SmsRestClientConfiguration smsRestClientConfiguration;

    public void sendSms(String to, String text) throws NoSuchAlgorithmException, InvalidKeyException {
        String body = restClient.post()
                .uri("/messages/v4/send-many/detail")
                .header("Authorization", smsRestClientConfiguration.createAuthorization())
                .body(Map.of("messages", List.of(new SmsSendClientDto(to, FROM_PHONE, text, RESERVATION_SUBJECT))))
                .retrieve()
                .body(String.class);
        System.out.println("client: " + body);
    }
}
