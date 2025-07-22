package fittoring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Value("${sms.base-url}")
    private String baseUrl;

    @Value("${sms.timeout.connect}")
    private int connectTimeout;

    @Value("${sms.timeout.read}")
    private int readTimeout;

    @Bean
    public RestClient smsRestClient(RestClient.Builder builder) {
        return builder.baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public RestClientCustomizer timeoutCustomizer() {
        return restClientBuilder -> restClientBuilder.requestFactory(simpleClientHttpRequestFactory());
    }

    private ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
