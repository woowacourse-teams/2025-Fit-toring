package fittoring.mentoring.business.model;

public enum ImageType {

    MENTORING_PROFILE,
    CERTIFICATE,
    NONE,
    ;

    public static String getDir(ImageType imageType) {
        if (imageType == MENTORING_PROFILE) {
            return "profile-image";
        }
        if (imageType == CERTIFICATE) {
            return "certificate-image";
        }
        return "none-type-image";
    }
}
