package fittoring.mentoring.business.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorMessage {

    MENTORING_NOT_FOUND("존재하지 않는 멘토링입니다."),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리가 포함되어 있습니다."),
    RESERVATION_NOT_FOUND("존재하지 않는 예약입니다."),
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
    IMAGE_NOT_FOUND("존재하지 않는 이미지입니다."),
    CERTIFICATE_NOT_FOUND("존재하지 않는 자격증명입니다."),
    ALREADY_PROCESSED_CERTIFICATE("이미 처리된 자격증명입니다."),
    MEMBER_NOT_FOUND("존재하지 않는 회원입니다."),
    REVIEWING_RESERVATION_NOT_FOUND("신청 이력이 있는 멘토링에만 리뷰를 남길 수 있습니다."),
    DUPLICATED_REVIEW("한 번의 예약당 한 번만 리뷰를 남길 수 있습니다."),
    FORBIDDEN_MEMBER("접근 권한이 없습니다."),
    RESERVATION_STATUS_ALREADY_UPDATE("이미 처리된 예약은 상태 변경이 불가합니다."),
    RESERVATION_STATUS_ALREADY_EQUAL("동일한 상태로는 변경할 수 없습니다."),
    STATUS_NOT_FOUND("존재하지 않는 상태입니다."),
    ;

    private final String message;

    BusinessErrorMessage(String message) {
        this.message = message;
    }
}
