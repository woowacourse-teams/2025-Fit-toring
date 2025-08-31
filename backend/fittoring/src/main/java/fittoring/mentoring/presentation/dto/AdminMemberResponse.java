package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;

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
