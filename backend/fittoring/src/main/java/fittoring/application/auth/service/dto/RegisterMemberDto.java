package fittoring.application.auth.service.dto;

import fittoring.domain.model.Gender;

public record RegisterMemberDto(
        String loginId,
        String name,
        Gender gender,
        String phoneNumber,
        String password
) {
}
