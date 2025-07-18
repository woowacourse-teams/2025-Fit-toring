package fittoring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestClient smsRestClient(@Autowired RestClient.Builder restClientBuilder) {
        return restClientBuilder.baseUrl("https://api.solapi.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    private RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder.requestFactory(simpleClientHttpRequestFactory());
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(1_200);
        simpleClientHttpRequestFactory.setReadTimeout(5_000);
        return simpleClientHttpRequestFactory;
    }
}
