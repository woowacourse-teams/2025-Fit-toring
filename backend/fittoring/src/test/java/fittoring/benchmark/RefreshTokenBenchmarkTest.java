package fittoring.benchmark;

import fittoring.IntegrationTestSupport;
import fittoring.application.auth.service.AuthService;
import fittoring.application.auth.service.dto.AuthTokenDto;
import fittoring.application.auth.service.dto.LoginInfoDto;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class RefreshTokenBenchmarkTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    private static final String RAW_PASSWORD = "BenchTest1234!";
    private static final int MEMBER_COUNT = 100;
    private static final int PRELOAD_LOGINS_PER_MEMBER = 30;
    private static final int MEASURE_COUNT = 500;
    private static final int CONCURRENT_THREADS = 10;
    private static final int OPS_PER_THREAD = 50;

    @DisplayName("Refresh Token 성능 벤치마크")
    @Test
    void benchmark() {
        // ========== 회원 생성 ==========
        List<Member> members = createMembers(MEMBER_COUNT);

        // ========== Pre-load: 토큰 누적 시뮬레이션 ==========
        // MEMBER_COUNT * PRELOAD_LOGINS_PER_MEMBER = 3000 토큰이 Redis에 적재
        long preloadStart = System.nanoTime();
        for (Member member : members) {
            for (int j = 0; j < PRELOAD_LOGINS_PER_MEMBER; j++) {
                authService.login(member.getLoginId(), RAW_PASSWORD);
            }
        }
        long preloadMs = (System.nanoTime() - preloadStart) / 1_000_000;
        int preloadCount = MEMBER_COUNT * PRELOAD_LOGINS_PER_MEMBER;

        // ========== 1. LOGIN (토큰 저장) 성능 측정 ==========
        long[] loginTimes = new long[MEASURE_COUNT];
        String[] refreshTokens = new String[MEASURE_COUNT];

        for (int i = 0; i < MEASURE_COUNT; i++) {
            Member member = members.get(i % MEMBER_COUNT);
            long start = System.nanoTime();
            LoginInfoDto result = authService.login(member.getLoginId(), RAW_PASSWORD);
            loginTimes[i] = System.nanoTime() - start;
            refreshTokens[i] = result.authTokenDto().refreshToken();
        }

        // ========== 2. REISSUE (토큰 재발급 - findByTokenValue) 성능 측정 ==========
        // 현재 구현은 Redis key 조회 + 회원 조회(JPA)를 함께 수행
        long[] reissueTimes = new long[MEASURE_COUNT];

        for (int i = 0; i < MEASURE_COUNT; i++) {
            long start = System.nanoTime();
            AuthTokenDto result = authService.reissue(refreshTokens[i]);
            reissueTimes[i] = System.nanoTime() - start;
            refreshTokens[i] = result.refreshToken(); // 갱신된 토큰으로 교체
        }

        // ========== 3. LOGOUT (회원별 전체 토큰 삭제) 성능 측정 ==========
        long[] logoutTimes = new long[MEMBER_COUNT];

        for (int i = 0; i < MEMBER_COUNT; i++) {
            long start = System.nanoTime();
            authService.logout(members.get(i).getId());
            logoutTimes[i] = System.nanoTime() - start;
        }

        // ========== 4. CONCURRENT 성능 측정 ==========
        // 로그아웃으로 토큰이 지워졌으므로 새로 로그인하여 토큰 생성
        List<Member> concurrentMembers = createMembers(CONCURRENT_THREADS * OPS_PER_THREAD,
                MEMBER_COUNT);
        long concurrentResult = benchmarkConcurrent(concurrentMembers);

        // ========== 결과 출력 ==========
        printSummary(preloadCount, preloadMs);
        printResult("로그인 (토큰 저장)", loginTimes);
        printResult("토큰 재발급 (토큰 조회)", reissueTimes);
        printResult("로그아웃 (토큰 삭제)", logoutTimes);
        printConcurrentResult(concurrentResult);
    }

    private long benchmarkConcurrent(List<Member> concurrentMembers) {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch ready = new CountDownLatch(CONCURRENT_THREADS);
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(CONCURRENT_THREADS);
        AtomicInteger errors = new AtomicInteger(0);

        int opsPerThread = concurrentMembers.size() / CONCURRENT_THREADS;

        for (int t = 0; t < CONCURRENT_THREADS; t++) {
            int threadOffset = t * opsPerThread;
            executor.submit(() -> {
                ready.countDown();
                try {
                    go.await();
                    for (int i = 0; i < opsPerThread; i++) {
                        try {
                            Member member = concurrentMembers.get(threadOffset + i);
                            LoginInfoDto loginResult = authService.login(
                                    member.getLoginId(), RAW_PASSWORD);
                            authService.reissue(loginResult.authTokenDto().refreshToken());
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

    private List<Member> createMembers(int count) {
        return createMembers(count, 0);
    }

    private List<Member> createMembers(int count, int startOffset) {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int idx = startOffset + i;
            Member member = new Member(
                    "benchuser" + idx,
                    Gender.MALE,
                    "User" + idx,
                    new Phone(phoneNumber(idx)),
                    Password.from(RAW_PASSWORD)
            );
            members.add(memberRepository.save(member));
        }
        return members;
    }

    private String phoneNumber(int index) {
        int part1 = 9000 + (index / 10000);
        int part2 = index % 10000;
        return String.format("010-%04d-%04d", part1, part2);
    }

    // ==================== Output ====================

    private void printSummary(int preloadCount, long preloadMs) {
        System.out.println();
        System.out.println("Refresh Token 성능 테스트 - Redis");
        System.out.printf("- 측정 건수: %d회%n", MEASURE_COUNT);
        System.out.printf("- 사전 적재 토큰: %d건 (%dms)%n",
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
