package fittoring.mentoring.business.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorMessage {

    MENTORING_NOT_FOUND("존재하지 않는 멘토링입니다."),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리가 포함되어 있습니다."),
    PHONE_INVALID("유효하지 않은 전화번호 형식입니다."),
    ;

    private final String message;

    BusinessErrorMessage(String message) {
        this.message = message;
    }
}
