package fittoring.exception;

import fittoring.mentoring.business.exception.CategoryNotFoundException;
import fittoring.mentoring.business.exception.DuplicateLoginIdException;
import fittoring.mentoring.business.exception.InvalidPhoneVerificationException;
import fittoring.mentoring.business.exception.InvalidStatusException;
import fittoring.mentoring.business.exception.InvalidTokenException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.exception.MisMatchPasswordException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.exception.NotFoundReservationException;
import fittoring.mentoring.business.exception.PasswordEncryptionException;
import fittoring.mentoring.infra.exception.S3UploadException;
import fittoring.mentoring.infra.exception.SmsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MentoringNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(MentoringNotFoundException e) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(CategoryNotFoundException e) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(NotFoundReservationException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundReservationException e) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidStatusException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handle(DuplicateLoginIdException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundMemberException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(MisMatchPasswordException.class)
    public ResponseEntity<ErrorResponse> handle(MisMatchPasswordException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidTokenException e) {
        return ErrorResponse.of(HttpStatus.UNAUTHORIZED, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(InvalidPhoneVerificationException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidPhoneVerificationException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handle(SystemException e) {
        logServerError(e);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(PasswordEncryptionException.class)
    public ResponseEntity<ErrorResponse> handle(PasswordEncryptionException e) {
        logServerError(e);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).toResponseEntity();
    }

    private void logServerError(Exception e) {
        log.error("[{}], [{}], [{}]", e.getClass(), e.getMessage(), e.getStackTrace());
    }

    @ExceptionHandler(SmsException.class)
    public ResponseEntity<ErrorResponse> handle(SmsException e) {
        logServerError(e);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        logServerError(e);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, SystemErrorMessage.INTERNAL_SERVER_ERROR.getMessage())
                .toResponseEntity();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NoHandlerFoundException e) {
        log.warn("[{}], [{}], [{}]", e.getClass(), e.getMessage(), e.getStackTrace());
        return ErrorResponse.of(HttpStatus.NOT_FOUND, SystemErrorMessage.RESOURCE_NOT_FOUND_ERROR.getMessage())
                .toResponseEntity();
    }

    @ExceptionHandler(S3UploadException.class)
    public ResponseEntity<ErrorResponse> handle(S3UploadException e) {
        logServerError(e);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).toResponseEntity();
    }
}
