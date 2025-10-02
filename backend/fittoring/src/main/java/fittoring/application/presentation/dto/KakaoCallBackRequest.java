package fittoring.application.presentation.dto;

public record KakaoCallBackRequest(
        String code,
        String redirectUrl,
        String error,
        String errorDescription,
        String state
) {
}
