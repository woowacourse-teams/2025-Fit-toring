package fittoring.mentoring.infra.image;

public record Encoded(
        byte[] bytes,
        String extension,
        String contentType
) {
}
