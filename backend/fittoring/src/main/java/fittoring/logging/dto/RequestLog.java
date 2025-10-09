package fittoring.logging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public record RequestLog(
        String event,
        String method,
        String uri,
        String queryString,
        String clientIp,
        String userAgent,
        JsonNode body,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime timestamp,
        String traceId
) {
}
