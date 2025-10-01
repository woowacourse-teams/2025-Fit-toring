package fittoring.mentoring.infra.image;

// TODO: api 작업 후 deprecated
public record Encoded(
        byte[] bytes,
        String extension,
        String contentType
) {
}
