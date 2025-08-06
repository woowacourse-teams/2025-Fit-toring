package fittoring.mentoring.business.model;

public enum MemberRole {

    MENTEE,
    MENTOR,
    ADMIN,
    ;

    public static boolean isMentorOrHigher(MemberRole role) {
        return role == MENTOR || role == ADMIN;
    }
}
