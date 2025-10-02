package fittoring.application.presentation.dto;

import fittoring.application.business.model.Member;
import fittoring.application.business.model.MemberRole;

public record AdminMemberResponse(
        String name,
        String loginId,
        String gender,
        String phoneNumber,
        MemberRole role
) {

    public static AdminMemberResponse from(Member member) {
        return new AdminMemberResponse(
                member.getName(),
                member.getLoginId(),
                member.getGender(),
                member.getPhoneNumber(),
                member.getRole()
        );
    }
}
