package fittoring.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittoring.logging.dto.ErrorLog;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CategoryNotFoundException;
import fittoring.application.exception.CertificateNotFoundException;
import fittoring.application.exception.DuplicateLoginIdException;
import fittoring.application.exception.DuplicatePhoneException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.InvalidCertificateException;
import fittoring.application.exception.InvalidCursorException;
import fittoring.application.exception.InvalidPhoneVerificationException;
import fittoring.application.exception.InvalidStatusException;
import fittoring.application.exception.InvalidTokenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.MentorAndMenteeIsSameException;
import fittoring.application.exception.MentoringAlreadyExistException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.exception.MisMatchPasswordException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.application.exception.NotFoundStatusException;
import fittoring.application.exception.PasswordEncryptionException;
import fittoring.application.exception.ReservationNotCompletedException;
import fittoring.application.exception.ReservationNotFoundException;
import fittoring.application.exception.ReviewAlreadyExistsException;
import fittoring.application.exception.ReviewNotFoundException;
import fittoring.application.exception.UnsupportedImageExtensionException;
import fittoring.application.infra.exception.S3UploadException;
import fittoring.application.infra.exception.SmsException;
import fittoring.application.presentation.exception.OauthLoginException;
import fittoring.util.ResponseDurationCalculator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
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

    @ExceptionHandler(InvalidCursorException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidCursorException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(CertificateNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handle(MissingRequestCookieException e) {
        return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage());
    }

    @ExceptionHandler(UnsupportedImageExtensionException.class)
    public ResponseEntity<ErrorResponse> handle(UnsupportedImageExtensionException e) {
        return buildErrorResponse(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
    }

    @ExceptionHandler(OauthLoginException.class)
    public ResponseEntity<ErrorResponse> handle(OauthLoginException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
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

        ErrorLog dto = new ErrorLog("ERROR", method, uri, durationMs, status.value(), e.getClass().getName(),
                e.getMessage(), stackToOneLine(e), normalizedUri, LocalDateTime.now(), traceId);
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
