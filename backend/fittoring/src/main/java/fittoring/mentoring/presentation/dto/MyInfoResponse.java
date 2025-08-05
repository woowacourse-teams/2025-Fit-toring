package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Member;

public record MyInfoResponse(
        String name,
        String gender,
        String phone
) {

    public static MyInfoResponse from(Member member) {
        return new MyInfoResponse(
                member.getName(),
                member.getGender(),
                member.getPhoneNumber()
        );
    }
}
