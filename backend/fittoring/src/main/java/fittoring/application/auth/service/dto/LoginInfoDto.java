package fittoring.application.auth.service.dto;

import fittoring.application.mentoring.presentation.dto.response.MemberLoginResponse;

public record LoginInfoDto(
        Long memberId,
        AuthTokenDto authTokenDto
) {
}
