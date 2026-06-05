package fittoring.application.member.presentation.dto.response;

import fittoring.domain.model.Gender;
import fittoring.domain.model.Image;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;

public record MyInfoResponse(
        String image,
        String loginId,
        String name,
        Gender gender,
        MemberRole myRole,
        String phoneNumber
) {

    public static MyInfoResponse from(Member member) {
        return new MyInfoResponse(
                null,
                member.getLoginId(),
                member.getName(),
                member.getGender(),
                member.getRole(),
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
                member.getRole(),
                member.getPhoneNumber()
        );
    }

    public static MyInfoResponse of(Member member, String imageUrl) {
        return new MyInfoResponse(
                imageUrl,
                member.getLoginId(),
                member.getName(),
                member.getGender(),
                member.getRole(),
                member.getPhoneNumber()
        );
    }
}
