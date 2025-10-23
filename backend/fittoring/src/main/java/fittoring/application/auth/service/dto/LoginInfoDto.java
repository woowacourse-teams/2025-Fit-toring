package fittoring.application.auth.service.dto;

public record LoginInfoDto(
        Long memberId,
        AuthTokenDto authTokenDto
) {
}
