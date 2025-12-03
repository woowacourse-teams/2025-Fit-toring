package fittoring.application.auth.service;

import fittoring.IntegrationTestSupport;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.repository.PhoneVerificationRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPhoneVerificationException;
import fittoring.domain.model.Phone;
import fittoring.domain.model.PhoneVerification;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class PhoneVerificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            List<PhoneVerification> phoneVerifications = jdbcTemplate.query(
                    "SELECT * FROM phone_verification WHERE phone_number = ?",
                    (rs, rowNum) -> new PhoneVerification(
                            new Phone(rs.getString("phone_number")),
                            rs.getString("code"),
                            rs.getTimestamp("expire_at").toLocalDateTime()
                    ),
                    phone.getNumber()
            );

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(phoneVerifications).hasSize(1);
                softAssertions.assertThat(phoneVerifications.get(0).getPhoneNumber()).isEqualTo(phoneNumber);
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
            phoneVerificationRepository.save(expectedExpireVerification);

            // when
            String phoneVerificationCode = phoneVerificationService.createPhoneVerification(phone);
            List<PhoneVerification> phoneVerifications = jdbcTemplate.query(
                    "SELECT * FROM phone_verification WHERE phone_number = ?",
                    (rs, rowNum) -> new PhoneVerification(
                            new Phone(rs.getString("phone_number")),
                            rs.getString("code"),
                            rs.getTimestamp("expire_at").toLocalDateTime()
                    ),
                    phone.getNumber()
            );

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(phoneVerifications).hasSize(1);
                softAssertions.assertThat(phoneVerifications.get(0).getPhoneNumber()).isEqualTo(phoneNumber);
                softAssertions.assertThat(phoneVerifications.get(0).getCode()).isEqualTo(phoneVerificationCode);
            });
        }
    }

    @DisplayName("전화번호 인증번호 확인")
    @Nested
    class VerifyCode {

        @DisplayName("전화번호 인증번호가 올바르고 유효시간 이내이면 예외가 발생하지 않는다.")
        @Test
        void validVerificationCode() {
            // given
            Phone phone = new Phone("010-1234-5678");
            PhoneVerification phoneVerification = new PhoneVerification(
                    phone,
                    "123456",
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(5)
            );
            phoneVerificationRepository.save(phoneVerification);
            VerificationCodeRequest request = new VerificationCodeRequest(
                    phone.getNumber(),
                    phoneVerification.getCode()
            );

            // when
            // then
            Assertions.assertThatCode(() -> phoneVerificationService.verifyCode(request))
                    .doesNotThrowAnyException();
        }

        @DisplayName("전화번호 인증번호가 올바르지 않으면 예외가 발생한다.")
        @Test
        void invalidVerificationCode() {
            // given
            Phone phone = new Phone("010-1234-5678");
            PhoneVerification phoneVerification = new PhoneVerification(
                    phone,
                    "123456",
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusMinutes(5)
            );
            phoneVerificationRepository.save(phoneVerification);
            VerificationCodeRequest request = new VerificationCodeRequest(
                    phone.getNumber(),
                    "invalidCode"
            );

            // when
            // then
            Assertions.assertThatThrownBy(() -> phoneVerificationService.verifyCode(request))
                    .isInstanceOf(InvalidPhoneVerificationException.class)
                    .hasMessage(BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage());
        }

        @DisplayName("전화번호 인증번호가 올바르고 유효시간 이후이면 예외가 발생한다.")
        @Test
        void validVerificationCodeButExpireTime() {
            // given
            Phone phone = new Phone("010-1234-5678");
            PhoneVerification phoneVerification = new PhoneVerification(
                    phone,
                    "123456",
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusMinutes(5)
            );
            phoneVerificationRepository.save(phoneVerification);
            VerificationCodeRequest request = new VerificationCodeRequest(
                    phone.getNumber(),
                    phoneVerification.getCode()
            );

            // when
            // then
            Assertions.assertThatThrownBy(() -> phoneVerificationService.verifyCode(request))
                    .isInstanceOf(InvalidPhoneVerificationException.class)
                    .hasMessage(BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage());
        }
    }
}
