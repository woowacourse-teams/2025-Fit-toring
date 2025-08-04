package fittoring.mentoring.business.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorMessage {

    MENTORING_NOT_FOUND("존재하지 않는 멘토링입니다."),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리가 포함되어 있습니다."),
    IMAGE_NOT_FOUND("존재하지 않는 이미지입니다."),
    CERTIFICATE_NOT_FOUND("존재하지 않는 자격증명입니다."),
    ALREADY_PROCESSED_CERTIFICATE("이미 처리된 자격증명입니다."),
    ;

    private final String message;

    BusinessErrorMessage(String message) {
        this.message = message;
    }
}
