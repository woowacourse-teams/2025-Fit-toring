package fittoring.mentoring.business.exception;

import lombok.Getter;

@Getter
public enum BusinessErrorMessage {

    MENTORING_NOT_FOUND("존재하지 않는 멘토링입니다."),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리가 포함되어 있습니다."),
    MEMBER_NOT_FOUND("존재하지 않는 멤버입니다."),
    REVIEW_NOT_FOUND("존재하지 않는 리뷰입니다."),
    REVIEWING_RESERVATION_NOT_FOUND("신청 이력이 있는 멘토링에만 리뷰를 남길 수 있습니다."),
    DUPLICATED_REVIEW("이미 리뷰를 남긴 멘토링입니다."),
    REVIEWER_NOT_SAME("자신이 남긴 리뷰가 아닙니다."),
    ;

    private final String message;

    BusinessErrorMessage(String message) {
        this.message = message;
    }
}
