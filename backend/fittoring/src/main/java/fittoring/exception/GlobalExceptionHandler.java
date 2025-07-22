package fittoring.exception;

import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.infra.exception.SmsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MentoringNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(MentoringNotFoundException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handle(SystemException e) {
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(SmsException.class)
    public ResponseEntity<ErrorResponse> handle(SmsException e) {
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()).toResponseEntity();
    }
}
