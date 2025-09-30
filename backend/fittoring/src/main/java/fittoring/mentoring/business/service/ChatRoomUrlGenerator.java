package fittoring.mentoring.business.service;

public class ChatRoomUrlGenerator {

    private final static String BASE_URL = "https://www.fittoring.com/chat/room/";

    public static String generate(Long chatRoomId) {
        return BASE_URL + chatRoomId;
    }
}
