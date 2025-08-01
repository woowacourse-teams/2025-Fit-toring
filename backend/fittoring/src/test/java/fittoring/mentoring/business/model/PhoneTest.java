package fittoring.mentoring.business.model;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidPhoneException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneTest {

    @DisplayName("형식에 맞지 않으면 전화번호가 생성 시 예외가 발생한다.")
    @Test
    void invalidPhone() {
        // given
        String phoneNumber = "invalidPhoneNumber";

        // when
        // then
        Assertions.assertThatThrownBy(() -> new Phone(phoneNumber))
                .isInstanceOf(InvalidPhoneException.class)
                .hasMessage(BusinessErrorMessage.PHONE_INVALID + phoneNumber);
    }

    @DisplayName("올바른 형식일 때 전화번호를 생성할 수 있다.")
    @Test
    void validPhone() {
        String phoneNumber = "010-1234-5678";

        // when
        // then
        Assertions.assertThatCode(() -> new Phone(phoneNumber))
                .doesNotThrowAnyException();
    }
}