package fittoring.application.infra.image;

public record Encoded(
        byte[] bytes,
        String extension,
        String contentType
) {
}
