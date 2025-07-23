package fittoring.mentoring.infra;

import fittoring.mentoring.infra.exception.InfraErrorMessage;
import fittoring.mentoring.infra.exception.SmsException;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class SmsRestClientServiceTest {

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

    @DisplayName("SMS 전송 실패 - 4xx 클라이언트 오류")
    @Test
    void sendSms_ClientError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));
        String to = "01012345678";
        String text = "Test Message";
        String subject = "Test Subject";

        // when
        // then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendSms(to, text, subject))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SENDING_ERROR.getMessage());
    }

    @DisplayName("SMS 전송 실패 - 5xx 서버 오류")
    @Test
    void sendSms_ServerError() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
        String to = "01012345678";
        String text = "Test Message";
        String subject = "Test Subject";

        // when
        // then
        Assertions.assertThatThrownBy(() -> smsRestClientService.sendSms(to, text, subject))
                .isInstanceOf(SmsException.class)
                .hasMessage(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
    }
}
