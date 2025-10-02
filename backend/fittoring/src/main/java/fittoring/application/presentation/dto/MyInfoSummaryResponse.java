package fittoring.application.presentation.dto;

import fittoring.application.business.model.Member;

public record MyInfoSummaryResponse(
    String name,
    String phoneNumber
) {

    public static MyInfoSummaryResponse of (Member member){
        return new MyInfoSummaryResponse(
                member.getName(),
                member.getPhoneNumber()
        );
    }
}
