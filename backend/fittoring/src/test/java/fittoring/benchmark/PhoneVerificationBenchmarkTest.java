package fittoring.benchmark;

import fittoring.IntegrationTestSupport;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.service.PhoneVerificationService;
import fittoring.domain.model.Phone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class PhoneVerificationBenchmarkTest extends IntegrationTestSupport {

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    private static final int WARMUP_COUNT = 50;
    private static final int MEASURE_COUNT = 1000;
    private static final int PRELOAD_COUNT = 3000;
    private static final int CONCURRENT_THREADS = 10;
    private static final int OPS_PER_THREAD = 100;

    @DisplayName("휴대폰 인증 성능 벤치마크")
    @Test
    void benchmark() {
        // ========== Warmup ==========
        for (int i = 0; i < WARMUP_COUNT; i++) {
            phoneVerificationService.createPhoneVerification(phone(i));
        }

        // ========== Pre-load: 기존 데이터 누적 시뮬레이션 ==========
        long preloadStart = System.nanoTime();
        for (int i = WARMUP_COUNT; i < WARMUP_COUNT + PRELOAD_COUNT; i++) {
            phoneVerificationService.createPhoneVerification(phone(i));
        }
        long preloadMs = (System.nanoTime() - preloadStart) / 1_000_000;

        int offset = WARMUP_COUNT + PRELOAD_COUNT;

        // ========== 1. CREATE 성능 측정 ==========
        long[] createTimes = new long[MEASURE_COUNT];
        Map<String, String> phoneToCodes = new HashMap<>();

        for (int i = 0; i < MEASURE_COUNT; i++) {
            String phoneNumber = phoneNumber(offset + i);
            long start = System.nanoTime();
            String code = phoneVerificationService.createPhoneVerification(new Phone(phoneNumber));
            createTimes[i] = System.nanoTime() - start;
            phoneToCodes.put(phoneNumber, code);
        }

        // ========== 2. VERIFY 성능 측정 ==========
        long[] verifyTimes = new long[MEASURE_COUNT];
        List<String> verifiedPhones = new ArrayList<>(phoneToCodes.keySet());

        for (int i = 0; i < MEASURE_COUNT; i++) {
            String phoneNum = verifiedPhones.get(i);
            String code = phoneToCodes.get(phoneNum);
            VerificationCodeRequest request = new VerificationCodeRequest(phoneNum, code);
            long start = System.nanoTime();
            phoneVerificationService.verifyCode(request);
            verifyTimes[i] = System.nanoTime() - start;
        }

        // ========== 3. CHECK STATUS 성능 측정 ==========
        long[] checkTimes = new long[MEASURE_COUNT];

        for (int i = 0; i < MEASURE_COUNT; i++) {
            String phoneNum = verifiedPhones.get(i);
            long start = System.nanoTime();
            phoneVerificationService.checkVerificationStatus(new Phone(phoneNum));
            checkTimes[i] = System.nanoTime() - start;
        }

        // ========== 4. CONCURRENT 성능 측정 ==========
        long concurrentResult = benchmarkConcurrent(offset + MEASURE_COUNT);

        // ========== 결과 출력 ==========
        printSummary(PRELOAD_COUNT, preloadMs);
        printResult("인증 코드 발급", createTimes);
        printResult("인증 코드 검증", verifyTimes);
        printResult("인증 상태 확인", checkTimes);
        printConcurrentResult(concurrentResult);
    }

    private long benchmarkConcurrent(int startOffset) {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch ready = new CountDownLatch(CONCURRENT_THREADS);
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(CONCURRENT_THREADS);
        AtomicInteger errors = new AtomicInteger(0);

        for (int t = 0; t < CONCURRENT_THREADS; t++) {
            int threadOffset = startOffset + (t * OPS_PER_THREAD);
            executor.submit(() -> {
                ready.countDown();
                try {
                    go.await();
                    for (int i = 0; i < OPS_PER_THREAD; i++) {
                        try {
                            String phoneNum = phoneNumber(threadOffset + i);
                            String code = phoneVerificationService.createPhoneVerification(new Phone(phoneNum));
                            phoneVerificationService.verifyCode(new VerificationCodeRequest(phoneNum, code));
                        } catch (Exception e) {
                            errors.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        try {
            ready.await();
            long start = System.nanoTime();
            go.countDown();
            done.await();
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            executor.shutdown();
            if (errors.get() > 0) {
                System.out.printf("  (concurrent errors: %d)%n", errors.get());
            }
            return elapsed;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdown();
            return -1;
        }
    }

    // ==================== Utility ====================

    private Phone phone(int index) {
        return new Phone(phoneNumber(index));
    }

    private String phoneNumber(int index) {
        int part1 = 1000 + (index / 10000);
        int part2 = index % 10000;
        return String.format("010-%04d-%04d", part1, part2);
    }

    // ==================== Output ====================

    private void printSummary(int preloadCount, long preloadMs) {
        System.out.println();
        System.out.println("Phone Verification 성능 테스트 - Redis");
        System.out.printf("- 측정 건수: %d회%n", MEASURE_COUNT);
        System.out.printf("- 사전 적재 데이터: %d건 (%dms)%n",
                preloadCount, preloadMs);
        System.out.println("--------------------------------------------------");
    }

    private void printResult(String label, long[] timesNano) {
        Arrays.sort(timesNano);

        long totalNano = 0;
        for (long t : timesNano) {
            totalNano += t;
        }

        double totalMs = totalNano / 1_000_000.0;
        double avgMs = totalMs / timesNano.length;
        double p95Ms = timesNano[percentileIndex(timesNano.length, 95)] / 1_000_000.0;
        double p99Ms = timesNano[percentileIndex(timesNano.length, 99)] / 1_000_000.0;
        double throughput = timesNano.length / (totalMs / 1000.0);

        System.out.println();
        System.out.printf("[%s]%n", label);
        System.out.printf("  총 소요 시간: %,.0fms%n", totalMs);
        System.out.printf("  평균 응답 시간: %.2fms/op%n", avgMs);
        System.out.printf("  p95: %.2fms%n", p95Ms);
        System.out.printf("  p99: %.2fms%n", p99Ms);
        System.out.printf("  처리량: %,.0fops/sec%n", throughput);
    }

    private void printConcurrentResult(long elapsedMs) {
        int totalOps = CONCURRENT_THREADS * OPS_PER_THREAD;
        double throughput = totalOps / (elapsedMs / 1000.0);

        System.out.println();
        System.out.printf("[동시성 테스트 (%d개 스레드 × %d회 = 총 %d회)]%n",
                CONCURRENT_THREADS, OPS_PER_THREAD, totalOps);
        System.out.printf("  총 소요 시간: %dms%n", elapsedMs);
        System.out.printf("  처리량: %,.0fops/sec%n", throughput);
        System.out.println("--------------------------------------------------");
    }

    private int percentileIndex(int length, int percentile) {
        return Math.min((int) Math.ceil(length * percentile / 100.0) - 1, length - 1);
    }
}
