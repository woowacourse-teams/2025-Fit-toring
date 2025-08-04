package fittoring.exception;

import lombok.Getter;

@Getter
public enum SystemErrorMessage {

    INTERNAL_SERVER_ERROR("서버에 심각한 오류가 발생했습니다."),
    RESOURCE_NOT_FOUND_ERROR("요청하신 URL이 존재하지 않습니다.")
    ;

    private final String message;

    SystemErrorMessage(String message) {
        this.message = message;
    }
}
