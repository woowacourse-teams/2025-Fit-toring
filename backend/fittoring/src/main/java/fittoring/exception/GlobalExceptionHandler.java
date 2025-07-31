package fittoring.exception;

import fittoring.mentoring.business.exception.CategoryNotFoundException;
import fittoring.mentoring.business.exception.DuplicateIdException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.infra.exception.SmsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(DuplicateIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateId(DuplicateIdException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handle(SystemException e) {
        logServerError(e);
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).toResponseEntity();
    }

    private void logServerError(Exception e) {
        log.error("ERROR: [{}], [{}]", e.getMessage(), (Object) e.getStackTrace());
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
}
