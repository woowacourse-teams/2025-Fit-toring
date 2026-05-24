package fittoring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.Phone;
import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import fittoring.infrastructure.SmsOutboxClaimer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class SmsOutboxClaimIntegrationTest extends AbstractApiDocumentationTest {

    private static final String SUBJECT = "핏토링 예약 알림";
    private static final int BATCH_SIZE = 10;
    private static final int LEASE_TIMEOUT_SECONDS = 300;

    @Autowired
    private SmsOutboxRepository smsOutboxRepository;

    @Autowired
    private SmsOutboxClaimer smsOutboxClaimer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("PENDING row를 claim하면 PROCESSING으로 마킹되고 processingStartedAt이 채워진다.")
    @Test
    void claimPendingMarksRowsAsProcessing() {
        // given
        SmsOutbox row = smsOutboxRepository.save(pendingRow());

        // when
        List<SmsOutbox> claimed = smsOutboxClaimer.claimPending();

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(claimed)
                    .extracting(SmsOutbox::getId)
                    .containsExactly(row.getId());
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.PROCESSING);
            softly.assertThat(refreshed.getProcessingStartedAt()).isNotNull();
        });
    }

    @DisplayName("lease 만료 전 PROCESSING row는 다시 claim되지 않는다.")
    @Test
    void freshProcessingRowIsNotReclaimed() {
        // given: 방금 PROCESSING으로 마킹된 row
        SmsOutbox row = pendingRow();
        row.markProcessing(LocalDateTime.now());
        smsOutboxRepository.save(row);

        // when
        List<SmsOutbox> claimed = smsOutboxClaimer.claimPending();

        // then
        assertThat(claimed).isEmpty();
    }

    @DisplayName("lease가 만료된 PROCESSING row는 다시 claim된다.")
    @Test
    void staleProcessingRowIsReclaimed() {
        // given: lease timeout보다 더 오래된 PROCESSING row
        SmsOutbox row = pendingRow();
        row.markProcessing(LocalDateTime.now().minusSeconds(LEASE_TIMEOUT_SECONDS + 10));
        SmsOutbox saved = smsOutboxRepository.save(row);

        // when
        List<SmsOutbox> claimed = smsOutboxClaimer.claimPending();

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(saved.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(claimed)
                    .extracting(SmsOutbox::getId)
                    .containsExactly(saved.getId());
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.PROCESSING);
            softly.assertThat(refreshed.getProcessingStartedAt())
                    .as("lease 회수 시점에 processingStartedAt이 갱신된다")
                    .isAfter(LocalDateTime.now().minusMinutes(1));
        });
    }

    @DisplayName("created_at이 동일한 row들은 id 오름차순으로 claim 순서가 결정된다.")
    @Test
    void claimablesAreOrderedByIdWhenCreatedAtIsTied() {
        // given: 두 row를 저장한 뒤 created_at을 동일 값으로 강제 갱신
        SmsOutbox first = smsOutboxRepository.save(pendingRow());
        SmsOutbox second = smsOutboxRepository.save(pendingRow());
        LocalDateTime sameInstant = LocalDateTime.now().minusSeconds(10);
        jdbcTemplate.update(
                "UPDATE sms_outbox SET created_at = ? WHERE id IN (?, ?)",
                sameInstant, first.getId(), second.getId()
        );

        // when
        List<SmsOutbox> claimed = smsOutboxClaimer.claimPending();

        // then: id 작은 row가 항상 먼저
        assertThat(claimed)
                .extracting(SmsOutbox::getId)
                .as("created_at 동률 시 id ASC로 순서가 결정적이어야 한다")
                .containsExactly(first.getId(), second.getId());
    }

    @DisplayName("FAILED row는 claim되지 않는다.")
    @Test
    void failedRowIsNotClaimed() {
        // given: 3회 실패해 FAILED로 격리된 row
        SmsOutbox row = pendingRow();
        for (int i = 0; i < 3; i++) {
            row.markProcessing(LocalDateTime.now());
            row.recordFailure("실패", 3);
        }
        smsOutboxRepository.save(row);

        // when
        List<SmsOutbox> claimed = smsOutboxClaimer.claimPending();

        // then
        assertThat(claimed).isEmpty();
    }

    @DisplayName("두 폴러가 동시에 claim을 호출해도 같은 row를 중복 claim하지 않는다.")
    @Test
    void parallelPollersDoNotClaimSameRows() throws Exception {
        // given: batch_size * 2 만큼 PENDING row를 준비
        int totalRows = BATCH_SIZE * 2;
        for (int i = 0; i < totalRows; i++) {
            smsOutboxRepository.save(pendingRow());
        }

        // when: 두 스레드가 동시에 claim 호출
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            CyclicBarrier barrier = new CyclicBarrier(2);   // 스레드 두 개가 출발선에 모일 때 까지 기다림
            Callable<List<Long>> claimTask = () -> {
                barrier.await(5, TimeUnit.SECONDS);
                return smsOutboxClaimer.claimPending().stream()
                        .map(SmsOutbox::getId)
                        .toList();
            };
            Future<List<Long>> first = executor.submit(claimTask);
            Future<List<Long>> second = executor.submit(claimTask);

            List<Long> firstIds = first.get(10, TimeUnit.SECONDS);
            List<Long> secondIds = second.get(10, TimeUnit.SECONDS);

            // then: 두 결과의 합집합 = 각 결과의 합 (= 중복 없음)
            Set<Long> union = firstIds.stream().collect(Collectors.toSet());
            union.addAll(secondIds);
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(union)
                        .as("동시 claim 결과에는 같은 outbox id가 두 번 등장하지 않는다")
                        .hasSize(firstIds.size() + secondIds.size());
                softly.assertThat(firstIds.size() + secondIds.size())
                        .as("두 claim 합쳐도 batch-size를 넘은 row는 다음 tick으로 미뤄진다")
                        .isLessThanOrEqualTo(totalRows);
            });
        } finally {
            executor.shutdownNow();
        }
    }

    private SmsOutbox pendingRow() {
        return SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-0000-0001"),
                "메시지 본문",
                SUBJECT
        );
    }
}
