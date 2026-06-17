package fittoring.application.reservation.sms;

/**
 * sms_outbox PENDING 행이 생성됐음을 알리는 이벤트.
 * 트랜잭션 커밋 후 SQS 빠른 길 발행 트리거로 사용된다.
 */
public record SmsOutboxCreatedEvent(Long outboxId) {
}
