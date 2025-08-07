package fittoring.mentoring.business.model;

public enum MemberRole {

    MENTEE,
    MENTOR,
    ADMIN,
    ;

    public static boolean isMentee(MemberRole role) {
        return role == MENTEE;
    }

    public static boolean isNotAdmin(MemberRole role) {
        return role != ADMIN;
    }
}
