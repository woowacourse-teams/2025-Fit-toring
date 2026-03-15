package fittoring.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String PROD = "https://www.fittoring.com";
    private static final String DEV = "https://dev.fittoring.com";
    private static final String LOCAL = "http://localhost:3000";

    private final InboundChannelInterceptor inboundChannelInterceptor;
    private final OutboundChannelInterceptor outboundChannelInterceptor;
    private final WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor;
    private final ChatStompErrorHandler chatStompErrorHandler;

    public WebSocketConfig(@Lazy InboundChannelInterceptor inboundChannelInterceptor,
                           @Lazy OutboundChannelInterceptor outboundChannelInterceptor,
                           @Lazy WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor,
                           @Lazy ChatStompErrorHandler chatStompErrorHandler) {
        this.inboundChannelInterceptor = inboundChannelInterceptor;
        this.outboundChannelInterceptor = outboundChannelInterceptor;
        this.webSocketAuthHandshakeInterceptor = webSocketAuthHandshakeInterceptor;
        this.chatStompErrorHandler = chatStompErrorHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        long[] heartbeats = {10000, 10000};
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(heartbeats)
                .setTaskScheduler(heartbeatScheduledTaskPool());

        config.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public TaskScheduler heartbeatScheduledTaskPool() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns(PROD, DEV, LOCAL)
                .addInterceptors(webSocketAuthHandshakeInterceptor);

        registry.setErrorHandler(chatStompErrorHandler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(inboundChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(outboundChannelInterceptor);
    }
}
