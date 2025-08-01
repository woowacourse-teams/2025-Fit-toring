package fittoring.mentoring.business.model;

import static org.assertj.core.api.Assertions.assertThat;

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
}
