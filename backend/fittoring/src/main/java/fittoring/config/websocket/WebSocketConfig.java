package fittoring.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String PROD = "https://www.fittoring.com";
    private static final String DEV = "https://dev.fittoring.com";
    private static final String LOCAL = "http://localhost:3000";
    private static final String ADMIN_PROD = "https://api.fittoring.com/web-admin";
    private static final String ADMIN_DEV = "https://devapi.fittoring.com/web-admin";

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;
    private final WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns(
                        PROD,
                        DEV,
                        LOCAL,
                        ADMIN_PROD,
                        ADMIN_DEV
                )
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
