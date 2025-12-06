package fittoring.application.exception;

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
    DUPLICATE_PHONE("이미 사용 중인 전화번호입니다. 다른 전화번호를 입력해주세요."),
    LOGIN_ID_NOT_FOUND("존재하지 않는 아이디입니다."),
    EXPIRED_TOKEN("유효시간이 만료된 토큰입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    EMPTY_TOKEN("토큰이 비어있습니다."),
    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다."),
    EMPTY_COOKIE("쿠키가 존재하지 않습니다."),
    PHONE_VERIFICATION_INVALID("만료 혹은 인증되지 않은 요청입니다."),
    IMAGE_NOT_FOUND("존재하지 않는 이미지입니다."),
    CERTIFICATE_NOT_FOUND("존재하지 않는 자격증명입니다."),
    ALREADY_PROCESSED_CERTIFICATE("이미 처리된 자격증명입니다."),
    MEMBER_NOT_FOUND("존재하지 않는 회원입니다."),
    REVIEWING_RESERVATION_NOT_FOUND("신청 이력이 있는 멘토링에만 리뷰를 남길 수 있습니다."),
    DUPLICATED_RESERVATION("이미 예약을 신청하였거나 진행 중인 멘토링입니다."),
    DUPLICATED_REVIEW("한 번의 예약당 한 번만 리뷰를 남길 수 있습니다."),
    FORBIDDEN_MEMBER("접근 권한이 없습니다."),
    RESERVATION_STATUS_ALREADY_UPDATE("이미 처리된 예약은 상태 변경이 불가합니다."),
    RESERVATION_STATUS_ALREADY_EQUAL("동일한 상태로는 변경할 수 없습니다."),
    STATUS_NOT_FOUND("존재하지 않는 상태입니다."),
    MENTORING_ALREADY_EXIST("이미 멘토링을 개설한 회원입니다."),
    CERTIFICATE_INFO_IMAGE_MISMATCH("자격증 정보와 자격증 이미지의 수가 일치하지 않습니다."),
    INVALID_CERTIFICATE_INFO("올바르지 않은 자격증 정보입니다."),
    REVIEW_NOT_FOUND("존재하지 않는 리뷰입니다."),
    NOT_REVIEW_OWNER("자신이 남긴 리뷰가 아닙니다."),
    MENTOR_NOT_SAME("자신이 개설한 멘토링이 아닙니다."),
    MENTOR_AND_MENTEE_IS_SAME("본인이 개설한 멘토링에는 예약할 수 없습니다."),
    RESERVATION_NOT_COMPLETED("멘토링이 완료된 후에만 리뷰를 남길 수 있습니다."),
    FORBIDDEN_URL("권한이 없는 URL 입니다"),
    NOT_CERTIFICATE_OWNER("자신의 자격사항이 아닙니다."),
    MEMBER_ROLE_NOT_FOUND("존재하지 않는 사용자 권한입니다. "),
    CHAT_ROOM_NOT_FOUND("존재하지 않는 채팅방 입니다."),
    CHAT_ROOM_ALREADY_EXISTS("이미 채팅방이 생성된 예약입니다."),
    UNAUTHORIZED_CHAT_ROOM_ACCESS("채팅방 접근 권한이 없습니다."),
    INVALID_STATUS_CHAT_ROOM_ACCESS("승인 또는 완료된 예약의 채팅방만 접속할 수 있습니다."),
    UNSUPPORTED_IMAGE_EXTENSION("지원하지 않는 확장자입니다."),
    ;

    private final String message;

    BusinessErrorMessage(String message) {
        this.message = message;
    }
}
