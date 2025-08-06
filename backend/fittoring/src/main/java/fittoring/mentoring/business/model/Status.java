package fittoring.mentoring.business.model;

import lombok.Getter;

@Getter
public enum Status {

    APPROVED("승인"),
    PENDING("대기"),
    REJECTED("거부"),
    COMPLETE("완료"),
    ;

    private final String value;

    Status(String name) {
        this.value = name;
    }

    public boolean isPending() {
        return this.equals(PENDING);
    }
}
