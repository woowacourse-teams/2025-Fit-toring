package fittoring.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.aspect.dto.ErrorLog;
import fittoring.mentoring.business.exception.CategoryNotFoundException;
import fittoring.mentoring.business.exception.CertificateNotFoundException;
import fittoring.mentoring.business.exception.DuplicateLoginIdException;
import fittoring.mentoring.business.exception.DuplicatePhoneException;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.exception.InvalidCertificateException;
import fittoring.mentoring.business.exception.InvalidPhoneVerificationException;
import fittoring.mentoring.business.exception.InvalidStatusException;
import fittoring.mentoring.business.exception.InvalidTokenException;
import fittoring.mentoring.business.exception.MemberNotFoundException;
import fittoring.mentoring.business.exception.MentorAndMenteeIsSameException;
import fittoring.mentoring.business.exception.MentoringAlreadyExistException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.MisMatchPasswordException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.exception.NotFoundStatusException;
import fittoring.mentoring.business.exception.PasswordEncryptionException;
import fittoring.mentoring.business.exception.ReservationNotCompletedException;
import fittoring.mentoring.business.exception.ReservationNotFoundException;
import fittoring.mentoring.business.exception.ReviewAlreadyExistsException;
import fittoring.mentoring.business.exception.ReviewNotFoundException;
import fittoring.mentoring.infra.exception.S3UploadException;
import fittoring.mentoring.infra.exception.SmsException;
import fittoring.util.ResponseDurationCalculator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.NoHandlerFoundException;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MentoringNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(MentoringNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ReservationNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(MemberNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(CategoryNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ReviewNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NotFoundStatusException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundStatusException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidStatusException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ResponseEntity<ErrorResponse> handle(DuplicatePhoneException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handle(DuplicateLoginIdException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundMemberException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MisMatchPasswordException.class)
    public ResponseEntity<ErrorResponse> handle(MisMatchPasswordException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(ReviewAlreadyExistsException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidTokenException e) {
        return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(InvalidPhoneVerificationException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidPhoneVerificationException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handle(ForbiddenException e) {
        return buildErrorResponse(e, HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handle(SystemException e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(PasswordEncryptionException.class)
    public ResponseEntity<ErrorResponse> handle(PasswordEncryptionException e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(SmsException.class)
    public ResponseEntity<ErrorResponse> handle(SmsException e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR,
                SystemErrorMessage.INTERNAL_SERVER_ERROR.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NoHandlerFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, SystemErrorMessage.RESOURCE_NOT_FOUND_ERROR.getMessage());
    }

    @ExceptionHandler(S3UploadException.class)
    public ResponseEntity<ErrorResponse> handle(S3UploadException e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(InvalidCertificateException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidCertificateException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MentoringAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handle(MentoringAlreadyExistException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MentorAndMenteeIsSameException.class)
    public ResponseEntity<ErrorResponse> handle(MentorAndMenteeIsSameException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ReservationNotCompletedException.class)
    public ResponseEntity<ErrorResponse> handle(ReservationNotCompletedException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(CertificateNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Throwable e, HttpStatus status, String message) {
        logErrorJson(e, status);
        return ErrorResponse.of(status, message).toResponseEntity();
    }

    private void logErrorJson(Throwable e, HttpStatus status) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        Long durationMs = ResponseDurationCalculator.calculate(attrs);
        String traceId = MDC.get("traceId");
        String method = MDC.get("method");
        String uri = MDC.get("uri");
        String normalizedUri = MDC.get("normalizedUri");

        ErrorLog dto = new ErrorLog(
                "ERROR",
                method,
                uri,
                durationMs,
                status.value(),
                e.getClass().getName(),
                e.getMessage(),
                stackToOneLine(e),
                normalizedUri,
                LocalDateTime.now(),
                traceId
        );
        try {
            String jsonLog = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
            if (status.is4xxClientError()) {
                log.warn(jsonLog);
            } else {
                log.error(jsonLog);
            }
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
