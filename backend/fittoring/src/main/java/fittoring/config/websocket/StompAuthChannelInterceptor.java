package fittoring.config.websocket;

import fittoring.config.auth.LoginInfo;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            LoginInfo loginInfo = (LoginInfo) Objects.requireNonNull(accessor.getSessionAttributes())
                    .get(AuthHandshakeInterceptor.LOGIN_INFO_KEY);
            accessor.setHeader(AuthHandshakeInterceptor.LOGIN_INFO_KEY, loginInfo);
        }
        return message;
    }
}
