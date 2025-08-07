package fittoring.mentoring.business.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StatusTest {

    @DisplayName("변경하려는 상태가 이미 처리된 상태인 경우 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource({
            "APPROVE, APPROVE",
            "REJECT, APPROVE"
    })
    void validate(Status currentStatus, String updateStatus) {
        //given
        //when
        //then
        assertThatThrownBy(() -> currentStatus.validate(Status.valueOf(updateStatus)))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_UPDATE.getMessage());
    }

    @DisplayName("변경하려는 상태가 현재 상태와 동일한 경우 예외가 발생한다.")
    @Test
    void validate2() {
        //given
        Status status = Status.PENDING;
        String updateStatus = "PENDING";

        //when
        //then
        assertThatThrownBy(() -> status.validate(Status.valueOf(updateStatus)))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_EQUAL.getMessage());
    }
}
