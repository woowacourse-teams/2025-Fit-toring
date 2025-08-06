package fittoring.mentoring.business.model;

import lombok.Getter;

@Getter
public enum Status {

    APPROVE("승인"),
    PENDING("대기"),
    REJECT("거절"),
    COMPLETE("완료"),
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }


    public void validateTransition(String newStatus) {
        if (this == APPROVE || this == REJECT) {
            throw new IllegalStateException("이미 처리된 예약은 상태 변경이 불가합니다.");
        }
        if (this.value.equals(newStatus)) {
            throw new IllegalStateException("동일한 상태로는 변경할 수 없습니다.");
        }
    }

    public boolean isPending() {
        return this.equals(PENDING);
    }
}
