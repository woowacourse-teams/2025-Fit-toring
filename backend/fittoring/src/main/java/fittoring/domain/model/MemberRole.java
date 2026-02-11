package fittoring.domain.model;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidMemberRoleException;
import java.util.Arrays;

public enum MemberRole {

    MENTEE,
    MENTOR,
    ADMIN,
    ;

    public static MemberRole of(String role) {
        return Arrays.stream(MemberRole.values())
                .filter(value -> value.name().equals(role))
                .findFirst()
                .orElseThrow(() -> new InvalidMemberRoleException(BusinessErrorMessage.ROLE_NOT_FOUND.getMessage()));
    }

    public static boolean isMentee(MemberRole role) {
        return role == MENTEE;
    }

    public static boolean isNotAdmin(MemberRole role) {
        return role != ADMIN;
    }
}
