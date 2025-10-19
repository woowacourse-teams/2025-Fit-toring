package fittoring.application.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomUrlGenerator {

    private static final String PROTOCOL = "https://";
    private static final String DOMAIN = ".fittoring.com";
    private static final String CHAT_PATH = "/chat/room/";

    @Value("${domain.sub-domain}")
    private String subDomain;


    public String generate(Long chatRoomId) {
        return PROTOCOL + subDomain + DOMAIN + CHAT_PATH + chatRoomId;
    }
}
