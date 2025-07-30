package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import fittoring.util.DbCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, AuthService.class})
@DataJpaTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("회원을 저장할 때 암호화된 비밀번호가 저장된다.")
    @Test
    void register() {
        //given
        String password = "password";
        SignUpRequest request = new SignUpRequest(
                "loginId",
                "이름",
                "남",
                "010-1234-5678",
                password);

        //when
        authService.register(request);

        //then
        String actual = testEntityManager.find(Member.class, 1L).getPassword();
        assertThat(actual).isNotEqualTo(password);
    }
}
