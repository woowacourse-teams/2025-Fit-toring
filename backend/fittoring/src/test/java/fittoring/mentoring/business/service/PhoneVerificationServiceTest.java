package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.PhoneVerification;
import fittoring.mentoring.infra.VerificationCodeGenerator;
import fittoring.util.DbCleaner;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@Import({DbCleaner.class, PhoneVerificationService.class, VerificationCodeGenerator.class})
@DataJpaTest
class PhoneVerificationServiceTest {

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("전화번호 인증번호 발급")
    @Nested
    class CreatePhoneVerification {

        @DisplayName("인증번호를 발급한다.")
        @Test
        void createVerificationCode() {
            // given
            String phoneNumber = "010-1234-5678";
            Phone phone = new Phone(phoneNumber);

            // when
            String phoneVerificationCode = phoneVerificationService.createPhoneVerification(phone);
            List<PhoneVerification> phoneVerifications = em.getEntityManager()
                    .createQuery("select p from PhoneVerification p where p.phone = :phone", PhoneVerification.class)
                    .setParameter("phone", phone)
                    .getResultList();

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(phoneVerifications).hasSize(1);
                softAssertions.assertThat(phoneVerifications.get(0).getPhone()).isEqualTo(phoneNumber);
                softAssertions.assertThat(phoneVerifications.get(0).getCode()).isEqualTo(phoneVerificationCode);
            });
        }

        @DisplayName("인증번호 발급 전 동일한 번호에 대한 인증번호가 존재하면 삭제한다.")
        @Test
        void deleteExpiredVerification() {
            // given
            String phoneNumber = "010-1234-5678";
            Phone phone = new Phone(phoneNumber);
            LocalDateTime expireTime = LocalDateTime.now().plusMinutes(3);
            PhoneVerification expectedExpireVerification = new PhoneVerification(
                    phone,
                    "123456",
                    expireTime
            );
            em.persist(expectedExpireVerification);

            // when
            String phoneVerificationCode = phoneVerificationService.createPhoneVerification(phone);
            List<PhoneVerification> phoneVerifications = em.getEntityManager()
                    .createQuery("select p from PhoneVerification p where p.phone = :phone", PhoneVerification.class)
                    .setParameter("phone", phone)
                    .getResultList();

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(phoneVerifications).hasSize(1);
                softAssertions.assertThat(phoneVerifications.get(0).getPhone()).isEqualTo(phoneNumber);
                softAssertions.assertThat(phoneVerifications.get(0).getCode()).isEqualTo(phoneVerificationCode);
            });
        }
    }
}
