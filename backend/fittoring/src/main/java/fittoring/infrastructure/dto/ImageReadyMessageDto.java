package fittoring.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;

public record ImageReadyMessageDto(
        @NotBlank
        String event,
        @NotBlank
        String imageType,
        @NotBlank
        String baseName,
        @NotBlank
        String imageVariant,
        @NotBlank
        String url
) {
}
