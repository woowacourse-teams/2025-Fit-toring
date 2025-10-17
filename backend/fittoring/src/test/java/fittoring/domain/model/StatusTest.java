package fittoring.domain.model;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusTest {

    @DisplayName("존재하지 않는 상태값으로 생성하면 예외가 발생한다.")
    @Test
    void of() {
        //given
        String inputStatus = "invalidStatus";

        //when
        //then
        assertThatThrownBy(() -> Status.of(inputStatus))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage(BusinessErrorMessage.STATUS_NOT_FOUND.getMessage());
    }

    @DisplayName("존재하는 상태값으로 생성하면 예외가 발생하지 않는다.")
    @Test
    void of2() {
        //given
        String inputStatus = "APPROVED";

        //when
        //then
        assertThatCode(() -> Status.of(inputStatus))
                .doesNotThrowAnyException();
    }
}
