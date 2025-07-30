package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Member;

public record SignUpRequest(
        String loginId,
        String name,
        String male,
        String phone,
        String password
) {

    public Member toEntity() {
        return new Member(loginId, name, male, phone, password);
    }
}
