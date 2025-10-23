package fittoring.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import fittoring.IntegrationTestSupport;
import fittoring.domain.model.Phone;
import fittoring.infrastructure.SmsAuthHeaderGenerator;
import fittoring.infrastructure.SmsRestClientService;
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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

class SmsRestClientIntegrationTest extends IntegrationTestSupport {

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

    @DisplayName("SMS API 타임아웃.")
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
                    new Phone("010-0000-0000"),
                    "connect timeout test",
                    "subject"
            ))
                    .isInstanceOf(ResourceAccessException.class);
        }

        @DisplayName("ReadTimeout 예외가 발생한다.")
        @Test
        void throwReadTimeout() {
            // given
            int delay = 100;
            int overReadTimeout = readTimeout + delay;
            mockWebServer.enqueue(new MockResponse()
                    .setBody("{\"result\":\"ok\"}")
                    .setBodyDelay(overReadTimeout, TimeUnit.MILLISECONDS));

            // when
            // then
            assertThatThrownBy(() -> smsRestClientService.sendSms(
                    new Phone("010-0000-0000"),
                    "read timeout test",
                    "subject"
            ))
                    .isInstanceOf(RestClientException.class);
        }
    }
}
