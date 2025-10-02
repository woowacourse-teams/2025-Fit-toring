package fittoring.logging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public record ResponseLog(
        String event,
        String method,
        String uri,
        Long durationMs,
        Integer statusCode,
        JsonNode body,
        String normalizedUri,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime timestamp,
        String traceId
) {
}
