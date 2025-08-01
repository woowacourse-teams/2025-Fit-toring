package fittoring.mentoring.business.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.exception.MisMatchPasswordException;
import fittoring.mentoring.business.model.password.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {

    @DisplayName("비밀번호를 해쉬 암호화 하여 생성할 수 있다.")
    @Test
    void from() {
        //given
        String password = "1234";

        //when
        Password actual = Password.from(password);

        //then
        assertThat(actual.getPassword()).isNotEqualTo(password);
    }

    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void validateMatches() {
        //given
        String inputPassword = "1234";

        Password password = Password.from("12345");
        //when
        //then
        assertThatThrownBy(() -> password.validateMatches(inputPassword))
                .isInstanceOf(MisMatchPasswordException.class);
    }

    @DisplayName("비밀번호가 일치하면 정상동작 한다.")
    @Test
    void validateMatches2() {
        //given
        String inputPassword = "1234";

        Password password = Password.from("1234");
        //when
        //then
        assertThatCode(() -> password.validateMatches(inputPassword))
                .doesNotThrowAnyException();
    }
}
