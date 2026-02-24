package fittoring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.Reservation;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Disabled("예약 삽입 동시성 테스트가 필요할 경우 활성화")
class MentoringReservationConcurrencyTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MentoringStatisticsRepository mentoringStatisticsRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("동일한 사용자가 동일한 멘토링에 대해 동시에 예약을 시도하면(따닥), 하나만 성공해야 한다.")
    @Test
    void createReservationConcurrencyWithRestAssured() throws InterruptedException {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    int statusCode = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .cookie("accessToken", accessToken)
                            .body(requestBody)
                            .when()
                            .post("/mentorings/" + mentoring.getId() + "/reservation")
                            .then()
                            .extract().statusCode();

                    if (statusCode == HttpStatus.CREATED.value() || statusCode == HttpStatus.OK.value()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(numberOfThreads - 1);
    }

    @DisplayName("종료된 멘토링에 대해서 다시 예약하면 성공한다.")
    @Test
    void createReservationByCompletedReservation2() {
        //given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        reservationRepository.save(FixtureUtil.testCompletedReservation(mentoring, mentee));

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        //when
        //then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(requestBody)
                .when()
                .post("/mentorings/" + mentoring.getId() + "/reservation")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("진행중인 멘토링이 존재할 때 동시에 예약이 들어오면 실패한다.")
    @Test
    void createReservationByCompletedReservation3() throws InterruptedException {
        //given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        reservationRepository.save(FixtureUtil.testApprovedReservation(mentoring, mentee));

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    int statusCode = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .cookie("accessToken", accessToken)
                            .body(requestBody)
                            .when()
                            .post("/mentorings/" + mentoring.getId() + "/reservation")
                            .then()
                            .extract().statusCode();

                    if (statusCode == HttpStatus.CREATED.value() || statusCode == HttpStatus.OK.value()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(0);
        assertThat(failCount.get()).isEqualTo(2);
    }

    @DisplayName("진행중인 멘토링이 존재할 때 같은 멘토링 예약을 할 수 없다.")
    @Test
    void createReservationByCompletedReservation4() throws InterruptedException {
        //given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        reservationRepository.save(FixtureUtil.testApprovedReservation(mentoring, mentee));

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        int numberOfThreads = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    int statusCode = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .cookie("accessToken", accessToken)
                            .body(requestBody)
                            .when()
                            .post("/mentorings/" + mentoring.getId() + "/reservation")
                            .then()
                            .extract().statusCode();

                    if (statusCode == HttpStatus.CREATED.value() || statusCode == HttpStatus.OK.value()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(0);
        assertThat(failCount.get()).isEqualTo(1);
    }

    @DisplayName("같은 사용자가 서로 다른 멘토링에 동시에 예약하면 모두 성공한다.")
    @Test
    void createReservationConcurrencyDifferentMentoring() throws InterruptedException {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring1 = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Mentoring mentoring2 = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring1));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring2));

        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        Long[] mentoringIds = {mentoring1.getId(), mentoring2.getId()};

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    int statusCode = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .cookie("accessToken", accessToken)
                            .body(requestBody)
                            .when()
                            .post("/mentorings/" + mentoringIds[index] + "/reservation")
                            .then()
                            .extract().statusCode();

                    if (statusCode == HttpStatus.CREATED.value() || statusCode == HttpStatus.OK.value()) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(2);
    }

    @DisplayName("서로 다른 사용자가 동일한 멘토링에 동시에 예약하면 모두 성공한다.")
    @Test
    void createReservationConcurrencyDifferentMentee() throws InterruptedException {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Member mentee1 = memberRepository.save(FixtureUtil.testMentee(1));
        Member mentee2 = memberRepository.save(FixtureUtil.testMentee(2));

        String token1 = jwtProvider.createAccessToken(mentee1.getId(), mentee1.getRole());
        String token2 = jwtProvider.createAccessToken(mentee2.getId(), mentee2.getRole());

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        String[] tokens = {token1, token2};

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    int statusCode = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .cookie("accessToken", tokens[index])
                            .body(requestBody)
                            .when()
                            .post("/mentorings/" + mentoring.getId() + "/reservation")
                            .then()
                            .extract().statusCode();

                    if (statusCode == HttpStatus.CREATED.value() || statusCode == HttpStatus.OK.value()) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(2);
    }

    @DisplayName("동시 예약 시 하나만 성공하면 통계 카운트도 1만 증가한다.")
    @Test
    void reservationStatisticsCountShouldBeOneWhenConcurrency() throws InterruptedException {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        MentoringStatistics statistics = mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    RestAssured.given()
                            .contentType(ContentType.JSON)
                            .cookie("accessToken", accessToken)
                            .body(requestBody)
                            .when()
                            .post("/mentorings/" + mentoring.getId() + "/reservation");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        MentoringStatistics refreshed = mentoringStatisticsRepository.findById(statistics.getMentoringId())
                .orElseThrow();
        assertThat(refreshed.getReservationCount()).isEqualTo(1);
    }

    @DisplayName("거절된 멘토링 예약이 존재할 때 다시 예약하면 성공한다.")
    @Test
    void createReservationByRejectedReservation() {
        //given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        mentoringStatisticsRepository.save(MentoringStatistics.defaultOf(mentoring));

        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        Reservation rejectedReservation = FixtureUtil.testPendingReservation(mentoring, mentee);
        rejectedReservation.reject();
        reservationRepository.save(rejectedReservation);

        ReservationCreateRequest requestBody = new ReservationCreateRequest("운동을 배우고 싶어요.");

        //when
        //then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(requestBody)
                .when()
                .post("/mentorings/" + mentoring.getId() + "/reservation")
                .then().log().all()
                .statusCode(201);
    }
}
