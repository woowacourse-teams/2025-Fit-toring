package fittoring.integration.mentoring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import fittoring.mentoring.infra.SmsAuthHeaderGenerator;
import fittoring.mentoring.infra.SmsRestClientService;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class SmsRestClientIntegrationTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private SmsRestClientService smsRestClientService;

    @Autowired
    private RestClient.Builder builder;

    @Value("${sms.timeout.read}")
    private int readTimeout;

    @BeforeAll
    static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8089);
    }

    @AfterAll
    static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @DisplayName("SMS API 타임아웃")
    @Nested
    class TimeoutTest {

        @DisplayName("ConnectTimeout 예외가 발생한다.")
        @Test
        void throwConnectTimeout() {
            // given
            RestClient restClient = builder.baseUrl("http://localhost:9999").build();
            SmsAuthHeaderGenerator dummyAuth = new SmsAuthHeaderGenerator(
                    "HMAC-SHA256",
                    "HmacSHA256",
                    "API_TEST_KEY",
                    "API_SECRET_KEY"
            );
            SmsRestClientService service = new SmsRestClientService(restClient, dummyAuth);

            // when
            // then
            assertThatThrownBy(() -> service.sendSms(
                    "010-0000-0000",
                    "subject",
                    "connect timeout test"
            ))
                    .isInstanceOf(ResourceAccessException.class);
        }

        @DisplayName("ReadTimeout 예외가 발생한다.")
        @Test
        void throwReadTimeout() {
            // given
            int overReadTimeout = readTimeout + 10;
            mockWebServer.enqueue(new MockResponse()
                    .setBody("{\"result\":\"ok\"}")
                    .setBodyDelay(overReadTimeout, TimeUnit.MILLISECONDS));

            // when
            // then
            assertThatThrownBy(() -> smsRestClientService.sendSms(
                    "010-0000-0000",
                    "subject",
                    "read timeout test"
            ))
                    .isInstanceOf(RestClientException.class);
        }
    }
}
