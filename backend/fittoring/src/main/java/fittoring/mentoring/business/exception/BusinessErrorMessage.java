package fittoring.mentoring.business.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorMessage {

    MENTORING_NOT_FOUND("존재하지 않는 멘토링입니다."),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리가 포함되어 있습니다."),
    PASSWORD_ENCRYPTION_FAILED("비밀번호 암호화에 실패했습니다."),
    MIS_MATCH_PASSWORD("비밀번호가 일치하지 않습니다."),
    DUPLICATE_LOGIN_ID("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");

    private final String message;

    BusinessErrorMessage(String message) {
        this.message = message;
    }
}
