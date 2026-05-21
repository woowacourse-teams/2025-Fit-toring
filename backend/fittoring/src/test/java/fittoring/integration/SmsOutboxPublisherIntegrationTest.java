package fittoring.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.Phone;
import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxEventType;
import fittoring.domain.model.SmsOutboxStatus;
import fittoring.infrastructure.SmsOutboxPublisher;
import fittoring.infrastructure.SmsOutboxResultApplier;
import fittoring.infrastructure.dto.BatchSendResult;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.SmsException;
import java.util.Set;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SmsOutboxPublisherIntegrationTest extends AbstractApiDocumentationTest {

    private static final String SUBJECT = "핏토링 예약 알림";

    @Autowired
    private SmsOutboxRepository smsOutboxRepository;

    @Autowired
    private SmsOutboxPublisher smsOutboxPublisher;

    @DisplayName("PENDING row가 SMS 발송 성공 시 SENT로 전환된다.")
    @Test
    void pendingRowMarkedAsSentOnSuccess() {
        // given
        doReturn(BatchSendResult.of(Set.of())).when(smsRestClientService).sendBatch(anyList());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));

        // when
        smsOutboxPublisher.publishPending();

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.SENT);
            softly.assertThat(refreshed.getAttempts()).isZero();
            softly.assertThat(refreshed.getLastError()).isNull();
            softly.assertThat(refreshed.getProcessingStartedAt())
                    .as("SENT row는 lease 정보를 보유하지 않는다")
                    .isNull();
        });
    }

    @DisplayName("배치에서 일부 row만 실패하면 성공 row는 SENT, 실패 row는 PENDING + attempts=1로 분기된다.")
    @Test
    void batchPartialFailureSplitsPerRowOutcome() {
        // given: 두 row를 저장하고, failure row의 outboxId만 실패로 stub
        SmsOutbox successRow = smsOutboxRepository.save(pendingRow("010-0000-0001"));
        SmsOutbox failureRow = smsOutboxRepository.save(pendingRow("010-0000-0002"));
        doReturn(BatchSendResult.of(Set.of(failureRow.getId())))
                .when(smsRestClientService).sendBatch(anyList());

        // when
        smsOutboxPublisher.publishPending();

        // then
        SmsOutbox refreshedSuccess = smsOutboxRepository.findById(successRow.getId()).orElseThrow();
        SmsOutbox refreshedFailure = smsOutboxRepository.findById(failureRow.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshedSuccess.getStatus()).isEqualTo(SmsOutboxStatus.SENT);
            softly.assertThat(refreshedSuccess.getAttempts()).isZero();
            softly.assertThat(refreshedFailure.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(refreshedFailure.getAttempts()).isEqualTo(1);
            softly.assertThat(refreshedFailure.getProcessingStartedAt()).isNull();
        });
    }

    @DisplayName("같은 수신번호를 가진 두 row 중 하나만 실패해도 outboxId 기반 매핑으로 정확히 분기된다.")
    @Test
    void sameRecipientResolvedByOutboxId() {
        // given: 같은 toPhone을 가진 두 row. failure row의 outboxId만 실패로 stub.
        SmsOutbox successRow = smsOutboxRepository.save(pendingRow("010-9999-9999"));
        SmsOutbox failureRow = smsOutboxRepository.save(pendingRow("010-9999-9999"));
        doReturn(BatchSendResult.of(Set.of(failureRow.getId())))
                .when(smsRestClientService).sendBatch(anyList());

        // when
        smsOutboxPublisher.publishPending();

        // then: toPhone이 동일해도 outboxId로 식별되어 정확히 분기
        SmsOutbox refreshedSuccess = smsOutboxRepository.findById(successRow.getId()).orElseThrow();
        SmsOutbox refreshedFailure = smsOutboxRepository.findById(failureRow.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshedSuccess.getStatus())
                    .as("동일 toPhone이라도 outboxId가 다르면 성공 row는 SENT")
                    .isEqualTo(SmsOutboxStatus.SENT);
            softly.assertThat(refreshedFailure.getStatus())
                    .as("같은 toPhone이라도 outboxId 매핑으로 실패 row만 PENDING으로 회수")
                    .isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(refreshedFailure.getAttempts()).isEqualTo(1);
        });
    }

    @DisplayName("PENDING row가 SMS 발송 실패 시 attempts=1 + 마지막 에러를 기록하고 status는 PENDING으로 남는다.")
    @Test
    void singleFailureKeepsRowPendingAndIncrementsAttempts() {
        // given: HTTP 자체가 실패한 시나리오 — 배치 전체 attempts++
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService).sendBatch(anyList());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));

        // when
        smsOutboxPublisher.publishPending();

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(refreshed.getAttempts()).isEqualTo(1);
            softly.assertThat(refreshed.getLastError())
                    .isEqualTo(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
            softly.assertThat(refreshed.getProcessingStartedAt())
                    .as("실패한 row는 lease를 반납하고 다음 폴링에 다시 잡힌다")
                    .isNull();
        });
    }

    @DisplayName("MAX_ATTEMPTS(3)회 연속 실패 시 status는 FAILED로 전환되고 마지막 에러가 기록된다.")
    @Test
    void maxedFailuresMarkRowAsFailed() {
        // given
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService).sendBatch(anyList());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));

        // when: 3회 폴링 (실패 누적)
        for (int i = 0; i < SmsOutboxResultApplier.MAX_ATTEMPTS; i++) {
            smsOutboxPublisher.publishPending();
        }

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(refreshed.getAttempts()).isEqualTo(SmsOutboxResultApplier.MAX_ATTEMPTS);
            softly.assertThat(refreshed.getLastError())
                    .isEqualTo(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
            softly.assertThat(refreshed.getProcessingStartedAt())
                    .as("FAILED row는 lease 정보를 보유하지 않는다")
                    .isNull();
        });
    }

    @DisplayName("SmsException이 아닌 예외(네트워크/runtime)도 attempts=1 + PENDING으로 회수되어 lease가 좀비 상태로 남지 않는다.")
    @Test
    void unexpectedRuntimeFailureKeepsRowPendingAndIncrementsAttempts() {
        // given
        doThrow(new RuntimeException("connection reset"))
                .when(smsRestClientService).sendBatch(anyList());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));

        // when
        smsOutboxPublisher.publishPending();

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus())
                    .as("비-SmsException도 catch되어 PROCESSING 좀비가 되지 않음")
                    .isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(refreshed.getAttempts()).isEqualTo(1);
            softly.assertThat(refreshed.getLastError())
                    .as("예외 메시지가 lastError에 기록된다")
                    .contains("connection reset");
            softly.assertThat(refreshed.getProcessingStartedAt()).isNull();
        });
    }

    @DisplayName("FAILED row는 더 이상 폴링 대상이 아니다.")
    @Test
    void failedRowsAreNotPicked() {
        // given: FAILED로 진입한 후 추가 폴링 호출 시 retry가 없어야 함을 검증
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService).sendBatch(anyList());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));
        for (int i = 0; i < SmsOutboxResultApplier.MAX_ATTEMPTS; i++) {
            smsOutboxPublisher.publishPending();
        }
        // 이 시점에 row는 FAILED 상태

        // when: 한 번 더 폴링
        smsOutboxPublisher.publishPending();

        // then: attempts는 더 증가하지 않아야 한다 (= FAILED는 후속 폴링 대상이 아님)
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(refreshed.getAttempts())
                    .as("FAILED row는 폴링 대상에서 제외되므로 attempts 변화 없음")
                    .isEqualTo(SmsOutboxResultApplier.MAX_ATTEMPTS);
        });
    }

    private SmsOutbox pendingRow(String toPhone) {
        return SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone(toPhone),
                "메시지 본문",
                SUBJECT
        );
    }
}
