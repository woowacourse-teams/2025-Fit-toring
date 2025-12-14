package fittoring.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.FixtureUtil;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReservationTest {

    @DisplayName("변경하려는 예약 상태가 이미 처리된 상태인 경우 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            "REJECTED, APPROVED"
    })
    void validateReservationStatus(String updateStatus) {
        //given
        Reservation reservation = FixtureUtil.getTestCompletedReservation(
                FixtureUtil.getTestMentoring(FixtureUtil.getTestMentor()),
                FixtureUtil.getTestMentee());

        //when
        //then
        assertThatThrownBy(() -> reservation.changeStatus(Status.valueOf(updateStatus)))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_UPDATE.getMessage());
    }

    @DisplayName("변경하려는 예약 상태가 현재 상태와 동일한 경우 예외가 발생한다.")
    @Test
    void validateReservationStatus2() {
        //given
        String updateStatus = "PENDING";

        Reservation reservation = FixtureUtil.getTestPendingReservation(
                FixtureUtil.getTestMentoring(FixtureUtil.getTestMentor()),
                FixtureUtil.getTestMentee());

        //when
        //then
        assertThatThrownBy(() -> reservation.changeStatus(Status.valueOf(updateStatus)))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_EQUAL.getMessage());
    }


}
