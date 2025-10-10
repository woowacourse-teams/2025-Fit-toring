package fittoring.application.member.presentation.dto.response;

import fittoring.domain.model.Image;
import fittoring.domain.model.Member;

public record MyInfoResponse(
        String image,
        String loginId,
        String name,
        String gender,
        String phoneNumber
) {

    public static MyInfoResponse from(Member member) {
        return new MyInfoResponse(
                null,
                member.getLoginId(),
                member.getName(),
                member.getGender(),
                member.getPhoneNumber()
        );
    }

    public static MyInfoResponse of(Member member, Image image) {
        if (image == null) {
            return MyInfoResponse.from(member);
        }
        return new MyInfoResponse(
                image.getUrl(),
                member.getLoginId(),
                member.getName(),
                member.getGender(),
                member.getPhoneNumber()
        );
    }
}
