package fittoring.mentoring.business.service;

public class ChatRoomUrlGenerator {

    private static final String BASE_URL = "https://www.fittoring.com/chat/room/";

    public static String generate(Long chatRoomId) {
        return BASE_URL + chatRoomId;
    }
}
