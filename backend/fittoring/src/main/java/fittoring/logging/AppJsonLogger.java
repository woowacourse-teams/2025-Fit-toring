package fittoring.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fittoring.logging.dto.AppLog;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class AppJsonLogger {

    private final ObjectMapper objectMapper;
    private final LogMaskingUtil logMaskingUtil;

    /**
     * INFO 로그 출력
     */
    public void info(String message) {
        write("INFO", message, null, null);
    }

    public void info(String message, Object body) {
        write("INFO", message, toJsonNode(body), null);
    }

    /**
     * WARN 로그 출력
     */
    public void warn(String message) {
        write("WARN", message, null, null);
    }

    public void warn(String message, Object body) {
        write("WARN", message, toJsonNode(body), null);
    }

    public void warn(String message, Object body, Throwable t) {
        write("WARN", message, toJsonNode(body), t);
    }

    /**
     * ERROR 로그 출력
     */
    public void error(String message) {
        write("ERROR", message, null, null);
    }

    public void error(String message, Object body) {
        write("ERROR", message, toJsonNode(body), null);
    }

    public void error(String message, Throwable t) {
        write("ERROR", message, null, t);
    }

    public void error(String message, Object body, Throwable t) {
        write("ERROR", message, toJsonNode(body), t);
    }

    /**
     * JSON 직렬화 및 로그 출력
     */
    private void write(String level, String message, JsonNode body, Throwable t) {
        JsonNode maskedBody = (body == null) ? null : logMaskingUtil.maskNode(body);
        JsonNode finalBody = (t == null) ? maskedBody : appendErrorLine(maskedBody, t);

        AppLog dto = new AppLog(
                "APP",
                level,
                MDC.get("method"),
                MDC.get("uri"),
                MDC.get("normalizedUri"),
                finalBody,
                message,
                LocalDateTime.now(),
                MDC.get("traceId")
        );

        try {
            String json = objectMapper.writeValueAsString(dto);
            logAtLevel(level, json);
        } catch (Exception e) {
            log.warn("JsonLogger 직렬화 실패: {}", message, e);
        }
    }

    /**
     * 객체를 JsonNode로 변환 (null-safe)
     */
    private JsonNode toJsonNode(Object body) {
        if (body == null) {
            return null;
        }
        try {
            return objectMapper.valueToTree(body);
        } catch (Exception e) {
            log.warn("Json 변환 실패: {}", body, e);
            return null;
        }
    }

    /**
     * SLF4J 로그 레벨에 따라 분기
     */
    private void logAtLevel(String level, String json) {
        switch (level) {
            case "ERROR" -> log.error(json);
            case "WARN" -> log.warn(json);
            default -> log.info(json);
        }
    }

    /**
     * 기존 body(JsonNode)에 error 요약 한 줄을 추가해서 반환
     */
    private JsonNode appendErrorLine(JsonNode body, Throwable t) {
        String errorLine = firstLine(t);
        ObjectNode root = (body instanceof ObjectNode obj)
                ? obj.deepCopy()
                : objectMapper.createObjectNode();
        root.put("error", errorLine);
        return root;
    }

    /**
     * "FullyQualifiedException: message" 한 줄 생성
     */
    private String firstLine(Throwable t) {
        String type = (t == null) ? "java.lang.Exception" : t.getClass().getName();
        String msg = (t == null || t.getMessage() == null) ? "" : t.getMessage();
        return msg.isEmpty() ? type : (type + ": " + msg);
    }
}
