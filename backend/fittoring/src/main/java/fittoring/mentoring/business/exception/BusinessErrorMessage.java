package fittoring.mentoring.business.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorMessage {

    MENTORING_NOT_FOUND("존재하지 않는 멘토링입니다."),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리가 포함되어 있습니다."),
    PHONE_INVALID("유효하지 않은 전화번호 형식입니다."),
    PASSWORD_ENCRYPTION_FAILED("비밀번호 암호화에 실패했습니다."),
    MIS_MATCH_PASSWORD("비밀번호가 일치하지 않습니다."),
    DUPLICATE_LOGIN_ID("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요."),
    LOGIN_ID_NOT_FOUND("존재하지 않는 아이디입니다."),
    EXPIRED_TOKEN("유효시간이 만료된 토큰입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    EMPTY_TOKEN("토큰이 비어있습니다."),
    NOT_FOUND_TOKEN("토큰을 찾을 수 없습니다."),
    EMPTY_COOKIE("쿠키가 존재하지 않습니다."),
    PHONE_VERIFICATION_INVALID("만료 혹은 인증되지 않은 요청입니다."),
    MEMBER_NOT_FOUND("존재하지 않는 멤버입니다."),
    REVIEWING_RESERVATION_NOT_FOUND("신청 이력이 있는 멘토링에만 리뷰를 남길 수 있습니다."),
    DUPLICATED_REVIEW("이미 리뷰를 남긴 멘토링입니다."),
    ;

    private final String message;

    BusinessErrorMessage(String message) {
        this.message = message;
    }
}
