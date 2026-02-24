package fittoring.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.logging.dto.ErrorLog;
import fittoring.util.ResponseDurationCalculator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Slf4j
@Component
public class ErrorJsonLogger {

    private static final String METHOD = "method";
    private static final String URI = "uri";
    private static final String NORMALIZED_URI = "normalizedUri";
    private static final String TRACE_ID = "traceId";

    private final ObjectMapper objectMapper;

    public void log(Throwable e, HttpStatus status) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long durationMs = ResponseDurationCalculator.calculate(attrs);
        String method = MDC.get(METHOD);
        String uri = MDC.get(URI);
        String normalizedUri = MDC.get(NORMALIZED_URI);
        String traceId = MDC.get(TRACE_ID);
        logWithContext(e, status, method, uri, normalizedUri, durationMs, traceId);
    }

    public void logWithContext(
            Throwable e,
            HttpStatus status,
            String method,
            String uri,
            String normalizedUri,
            Long durationMs,
            String traceId
    ) {
        logWithContext(e, status, method, uri, normalizedUri, durationMs, traceId, e.getMessage());
    }

    public void logWithContext(
            Throwable e,
            HttpStatus status,
            String method,
            String uri,
            String normalizedUri,
            Long durationMs,
            String traceId,
            String message
    ) {
        String finalNormalizedUri = (normalizedUri == null || normalizedUri.isBlank())
                ? uri
                : normalizedUri;
        ErrorLog dto = new ErrorLog(
                "ERROR",
                method,
                uri,
                durationMs,
                status.value(),
                e.getClass().getName(),
                message,
                stackToOneLine(e),
                finalNormalizedUri,
                LocalDateTime.now(),
                traceId
        );
        try {
            String jsonLog = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
            if (status.is4xxClientError()) {
                log.warn(jsonLog);
                return;
            }
            log.error(jsonLog);
        } catch (Exception ex) {
            log.error("에러로그 직렬화 실패", ex);
        }
    }

    private String stackToOneLine(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement el : e.getStackTrace()) {
            sb.append(el).append(" | ");
        }
        return sb.toString();
    }
}
