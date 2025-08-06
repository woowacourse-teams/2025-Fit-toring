package fittoring.mentoring.business.model;

import lombok.Getter;

@Getter
public enum Status {

    APPROVE("승인"),
    PENDING("대기"),
    REJECT("거부"),
    COMPLETE("완료"),
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public boolean isPending() {
        return this.equals(PENDING);
    }
}
