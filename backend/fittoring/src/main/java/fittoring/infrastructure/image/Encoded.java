package fittoring.infrastructure.image;

public record Encoded(
        byte[] bytes,
        String extension,
        String contentType
) {
}
