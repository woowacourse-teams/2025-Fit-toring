package fittoring.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.IntegrationTestSupport;
import fittoring.domain.model.Phone;
import fittoring.infrastructure.dto.BatchSendResult;
import fittoring.infrastructure.dto.SmsOutboxMessage;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.SmsException;
import java.io.IOException;
import java.util.List;

import fittoring.infrastructure.sms.SmsAuthHeaderGenerator;
import fittoring.infrastructure.sms.SmsRestClientService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

class SmsRestClientServiceTest extends IntegrationTestSupport {

    private static MockWebServer mockWebServer;

    private static SmsRestClientService smsRestClientService;

    @BeforeAll
    static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8089);

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString()) // Mock Server URL
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        SmsAuthHeaderGenerator dummyAuth = new SmsAuthHeaderGenerator(
                "HMAC-SHA256",
                "HmacSHA256",
                "API_TEST_KEY",
                "API_SECRET_KEY"
        );
        smsRestClientService = new SmsRestClientService(restClient, dummyAuth);
    }

    @AfterAll
    static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @DisplayName("장문 SMS 전송 실패 - 4xx 클라이언트 오류")
    @Test
    void sendSms_ClientError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));
        String to = "010-1234-5678";
        Phone toPhone = new Phone(to);
        String text = "Test Message";
        String subject = "Test Subject";

        // when
        // then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendSms(toPhone, text, subject))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
    }

    @DisplayName("장문 SMS 전송 실패 - 5xx 서버 오류")
    @Test
    void sendSms_ServerError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
        String to = "010-1234-5678";
        Phone toPhone = new Phone(to);
        String text = "Test Message";
        String subject = "Test Subject";

        // when
        // then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendSms(toPhone, text, subject))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
    }

    @DisplayName("단문 SMS 전송 실패 - 4xx 클라이언트 오류")
    @Test
    void sendShortSms_ClientError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));
        String to = "010-1234-5678";
        Phone toPhone = new Phone(to);
        String text = "Test Message";

        // when
        // then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendSms(toPhone, text))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
    }

    @DisplayName("단문 SMS 전송 실패 - 5xx 서버 오류")
    @Test
    void sendShortSms_ServerError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
        String to = "010-1234-5678";
        Phone toPhone = new Phone(to);
        String text = "Test Message";

        // when
        // then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendSms(toPhone, text))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
    }

    @DisplayName("배치 전송 - 모든 메시지가 성공하면 실패 outboxId 목록은 비어 있다")
    @Test
    void sendBatch_allSuccess() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "groupInfo": {},
                          "failedMessageList": []
                        }
                        """));
        List<SmsOutboxMessage> messages = List.of(
                new SmsOutboxMessage(100L, new Phone("010-1111-1111"), "메시지1", "subject"),
                new SmsOutboxMessage(101L, new Phone("010-2222-2222"), "메시지2", "subject")
        );

        // when
        BatchSendResult result = smsRestClientService.sendBatch(messages);

        // then
        assertThat(result.failedOutboxIds()).isEmpty();
    }

    @DisplayName("배치 전송 - failedMessageList의 customFields.outboxId가 결과에 담긴다")
    @Test
    void sendBatch_partialFailureMapsByOutboxId() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "groupInfo": {},
                          "failedMessageList": [
                            {
                              "to": "010-2222-2222",
                              "statusCode": "3040",
                              "statusMessage": "INVALID_NUMBER",
                              "customFields": { "outboxId": "101" }
                            }
                          ]
                        }
                        """));
        List<SmsOutboxMessage> messages = List.of(
                new SmsOutboxMessage(100L, new Phone("010-1111-1111"), "메시지1", "subject"),
                new SmsOutboxMessage(101L, new Phone("010-2222-2222"), "메시지2", "subject")
        );

        // when
        BatchSendResult result = smsRestClientService.sendBatch(messages);

        // then
        assertThat(result.isFailed(101L)).isTrue();
        assertThat(result.isFailed(100L)).isFalse();
    }

    @DisplayName("배치 전송 - 같은 수신번호를 가진 두 row 중 하나만 실패해도 outboxId로 정확히 분기된다")
    @Test
    void sendBatch_sameRecipientResolvedByOutboxId() {
        // given: 같은 to phone에 outboxId 200만 실패
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "groupInfo": {},
                          "failedMessageList": [
                            {
                              "to": "010-9999-9999",
                              "statusCode": "3040",
                              "statusMessage": "...",
                              "customFields": { "outboxId": "200" }
                            }
                          ]
                        }
                        """));
        List<SmsOutboxMessage> messages = List.of(
                new SmsOutboxMessage(200L, new Phone("010-9999-9999"), "메시지A", "subject"),
                new SmsOutboxMessage(201L, new Phone("010-9999-9999"), "메시지B", "subject")
        );

        // when
        BatchSendResult result = smsRestClientService.sendBatch(messages);

        // then: 같은 to phone이라도 outboxId로 식별
        assertThat(result.isFailed(200L)).isTrue();
        assertThat(result.isFailed(201L)).isFalse();
    }

    @DisplayName("배치 전송 - 4xx 클라이언트 오류 시 SmsException")
    @Test
    void sendBatch_clientError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));
        List<SmsOutboxMessage> messages = List.of(
                new SmsOutboxMessage(100L, new Phone("010-1111-1111"), "메시지", "subject")
        );

        // when //then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendBatch(messages))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
    }

    @DisplayName("배치 전송 - 5xx 서버 오류 시 SmsException")
    @Test
    void sendBatch_serverError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
        List<SmsOutboxMessage> messages = List.of(
                new SmsOutboxMessage(100L, new Phone("010-1111-1111"), "메시지", "subject")
        );

        // when //then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendBatch(messages))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
    }
}
