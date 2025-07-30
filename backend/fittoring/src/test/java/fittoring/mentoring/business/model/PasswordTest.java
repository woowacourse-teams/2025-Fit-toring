package fittoring.mentoring.business.model;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.mentoring.business.model.password.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {

    @DisplayName("비밀번호를 해쉬 암호화 하여 생성할 수 있다.")
    @Test
    void createEncrypt() {
        //given
        String password = "1234";

        //when
        Password actual = Password.createEncrypt(password);

        //then
        assertThat(actual.getPassword()).isNotEqualTo(password);
    }

    @DisplayName("암호화된 비밀번호를 그대로 생성할 수 있다.")
    @Test
    void formEncrypt() {
        //given
        String password = "1234";
        Password encryptPassword = Password.createEncrypt(password);

        //when
        Password actual = Password.formEncrypt(encryptPassword.getPassword());

        //then
        assertThat(actual).isEqualTo(encryptPassword);
    }
}
