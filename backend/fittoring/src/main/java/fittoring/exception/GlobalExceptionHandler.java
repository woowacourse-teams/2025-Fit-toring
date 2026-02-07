package fittoring.exception;

import fittoring.application.exception.*;
import fittoring.infrastructure.exception.S3UploadException;
import fittoring.infrastructure.exception.SmsException;
import fittoring.logging.ErrorJsonLogger;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorJsonLogger errorJsonLogger;

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

    @ExceptionHandler(EmptyRequestException.class)
    public ResponseEntity<ErrorResponse> handle(EmptyRequestException e) {
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
        String message = getRequestValidExceptionMessage(e);
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(ReviewAlreadyExistsException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handle(UnauthorizedException e) {
        return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, e.getMessage());
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

    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ChatRoomNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ChatRoomAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(ChatRoomAlreadyExistsException e) {
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedChatRoomAccessException.class)
    public ResponseEntity<ErrorResponse> handle(UnauthorizedChatRoomAccessException e) {
        return buildErrorResponse(e, HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(InvalidMemberRoleException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidMemberRoleException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DuplicateDeviceException.class)
    public ResponseEntity<ErrorResponse> handle(DuplicateDeviceException e) {
        return buildErrorResponse(e, HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Throwable e, HttpStatus status, String message) {
        errorJsonLogger.log(e, status);
        return ErrorResponse.of(status, message).toResponseEntity();
    }

    private String getRequestValidExceptionMessage(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");
    }

}
