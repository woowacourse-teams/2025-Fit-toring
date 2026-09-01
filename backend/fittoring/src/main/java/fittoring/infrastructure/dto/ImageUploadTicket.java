package fittoring.infrastructure.dto;

/**
 * Redis에 저장된 채팅 이미지 업로드 티켓 값({@code memberId:chatRoomId:s3Key})을 표현한다.
 */
public record ImageUploadTicket(String memberId, String chatRoomId, String s3Key) {

    private static final String VALUE_DELIMITER = ":";
    private static final int FIELD_COUNT = 3;

    public static ImageUploadTicket of(Long memberId, Long chatRoomId, String s3Key) {
        return new ImageUploadTicket(String.valueOf(memberId), String.valueOf(chatRoomId), s3Key);
    }

    public static ImageUploadTicket parse(String storedValue) {
        if (storedValue == null) {
            return null;
        }
        String[] fields = storedValue.split(VALUE_DELIMITER, FIELD_COUNT);
        if (fields.length != FIELD_COUNT) {
            return null;
        }
        return new ImageUploadTicket(fields[0], fields[1], fields[2]);
    }

    public String serialize() {
        return String.join(VALUE_DELIMITER, memberId, chatRoomId, s3Key);
    }

    public boolean isOwnedBy(Long memberId, Long chatRoomId) {
        return this.memberId.equals(String.valueOf(memberId))
                && this.chatRoomId.equals(String.valueOf(chatRoomId));
    }
}
