package fittoring.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        HttpStatus status,
        String message,
        LocalDateTime timestamp
) {

    public static ErrorResponse of(HttpStatus httpStatus, String errorMessage){
        return new ErrorResponse(
                httpStatus,
                errorMessage,
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );
    }

    public ResponseEntity<ErrorResponse> toResponseEntity() {
        return ResponseEntity.status(status)
                .body(this);
    }
}
