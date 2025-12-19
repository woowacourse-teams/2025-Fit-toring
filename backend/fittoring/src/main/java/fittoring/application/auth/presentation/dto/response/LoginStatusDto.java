package fittoring.application.auth.presentation.dto.response;

public record LoginStatusDto(
        boolean isLoggedIn,
        Long memberId
) {
}
