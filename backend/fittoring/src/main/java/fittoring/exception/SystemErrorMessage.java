package fittoring.exception;

import lombok.Getter;

@Getter
public enum SystemErrorMessage {

    INTERNAL_SERVER_ERROR("서버에 심각한 오류가 발생했습니다."),
    ;

    private final String message;

    SystemErrorMessage(String message) {
        this.message = message;
    }
}
