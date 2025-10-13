package fittoring.logging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public record AppLog(
        String event,
        String level,
        String method,
        String uri,
        String normalizedUri,
        JsonNode body,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime timestamp,
        String traceId
) {
}
