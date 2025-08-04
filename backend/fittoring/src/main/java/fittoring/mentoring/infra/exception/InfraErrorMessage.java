package fittoring.mentoring.infra.exception;

import lombok.Getter;

@Getter
public enum InfraErrorMessage {

    SMS_SENDING_ERROR("SMS 메시지 전송 과정에서 오류가 발생했습니다."),
    SMS_SERVER_ERROR("SMS 서버에 오류가 발생했습니다."),
    S3_UPLOAD_ERROR("S3 파일 업로드 과정에서 오류가 발생했습니다."),
    ;

    private final String message;

    InfraErrorMessage(String message) {
        this.message = message;
    }
}
