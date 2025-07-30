package fittoring.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* fittoring..*Controller.*(..))")
    public void controller() {
    }

    @Before("controller()")
    public void logBeforeApiCall(final JoinPoint joinPoint) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        logHttpInfo(attributes, "REQUEST", null);
    }

    private void logHttpInfo(ServletRequestAttributes attributes, String prefix, String body) {
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String httpMethod = request.getMethod();
            String requestUri = request.getRequestURI();
            String queryString = request.getQueryString();

            String clientIp = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");

            log.info(
                    "{}: time=[{}] client=[{}:{}] request=[{} {} query={} body={}]",
                    prefix,
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")),
                    clientIp,
                    userAgent,
                    httpMethod,
                    requestUri,
                    queryString,
                    body
            );
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @AfterReturning(pointcut = "controller()", returning = "result")
    public void logAfterApiCall(final JoinPoint joinPoint, final Object result) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logHttpInfo(attributes, "RESPONSE", objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("RESPONSE JSON 포매팅 실패: {}", result, e);
        }
    }

    @AfterThrowing(pointcut = "controller()", throwing = "e")
    public void afterThrowingController(JoinPoint joinPoint, Throwable e) {
        Method method = getMethod(joinPoint);
        log.warn("WARN: [{}] [{}]", method.getName(), e.getMessage());
        log.warn("WARN: {}", (Object) e.getStackTrace());
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
