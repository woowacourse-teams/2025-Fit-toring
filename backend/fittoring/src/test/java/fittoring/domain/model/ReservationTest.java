package fittoring.domain.model;

import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    @DisplayName("승인하려는 예약 상태가 이미 처리된 상태인 경우 예외가 발생한다.")
    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"REJECTED", "APPROVED", "COMPLETE"})
    void approveValidate(Status status) {
        //given
        Mentoring mentoring = FixtureUtil.getTestMentoring(FixtureUtil.getTestMentor());
        Member mentee = FixtureUtil.getTestMentee();

        Reservation reservation = new Reservation("내용", status, mentoring, mentee);

        //when
        //then
        assertThatThrownBy(reservation::approve)
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_UPDATE.getMessage());
    }

    @DisplayName("거절하려는 예약 상태가 이미 처리된 상태인 경우 예외가 발생한다.")
    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"REJECTED", "APPROVED", "COMPLETE"})
    void rejectValidate(Status status) {
        //given
        Mentoring mentoring = FixtureUtil.getTestMentoring(FixtureUtil.getTestMentor());
        Member mentee = FixtureUtil.getTestMentee();

        Reservation reservation = new Reservation("내용", status, mentoring, mentee);

        //when
        //then
        assertThatThrownBy(reservation::reject)
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_UPDATE.getMessage());
    }

    @DisplayName("대기 상태의 예약만 승인이 가능하다.")
    @Test
    void approve() {
        //given
        Mentoring mentoring = FixtureUtil.getTestMentoring(FixtureUtil.getTestMentor());
        Member mentee = FixtureUtil.getTestMentee();
        Reservation reservation = FixtureUtil.getTestPendingReservation(mentoring, mentee);

        //when
        //then
        assertThatCode(reservation::approve)
                .doesNotThrowAnyException();
    }

    @DisplayName("대기 상태의 예약만 거절이 가능하다.")
    @Test
    void reject() {
        //given
        Mentoring mentoring = FixtureUtil.getTestMentoring(FixtureUtil.getTestMentor());
        Member mentee = FixtureUtil.getTestMentee();
        Reservation reservation = FixtureUtil.getTestPendingReservation(mentoring, mentee);

        //when
        //then
        assertThatCode(reservation::reject)
                .doesNotThrowAnyException();
    }


}
