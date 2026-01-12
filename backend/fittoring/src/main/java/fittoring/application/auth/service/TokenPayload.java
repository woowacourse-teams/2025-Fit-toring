package fittoring.application.auth.service;

public record TokenPayload(
        Long sub,
        String role
) {
}
