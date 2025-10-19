package fittoring.domain.model;

public enum ImageType {

    MENTORING_PROFILE,
    CERTIFICATE,
    CHAT,
    NONE,
    ;

    public static String getDir(ImageType imageType) {
        if (imageType == MENTORING_PROFILE) {
            return "profile-image";
        }
        if (imageType == CERTIFICATE) {
            return "certificate-image";
        }
        if (imageType == CHAT) {
            return "chat-image";
        }
        return "none-type-image";
    }

    public static ImageType fromDir(String dir) {
        if ("profile-image".equals(dir)) {
            return MENTORING_PROFILE;
        }
        if ("certificate-image".equals(dir)) {
            return CERTIFICATE;
        }
        if ("chat-image".equals(dir)) {
            return CHAT;
        }
        return NONE;
    }
}
