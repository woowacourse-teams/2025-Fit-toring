package fittoring.application.member.service.dto;

import fittoring.application.auth.service.dto.AuthTokenDto;

public record RegisterOAuthDto(
        Long memberId,
        AuthTokenDto authTokenDto
) {
}
