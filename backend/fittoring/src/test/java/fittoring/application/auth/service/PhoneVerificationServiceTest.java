package fittoring.application.auth.service;

import fittoring.IntegrationTestSupport;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.repository.PhoneVerificationData;
import fittoring.application.auth.repository.PhoneVerificationRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidPhoneVerificationException;
import fittoring.domain.model.Phone;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PhoneVerificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;

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
            String code = phoneVerificationService.createPhoneVerification(phone);

            // then
            Optional<PhoneVerificationData> result = phoneVerificationRepository.findByPhone(phoneNumber);
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(result).isPresent();
                softAssertions.assertThat(result.get().phoneNumber()).isEqualTo(phoneNumber);
                softAssertions.assertThat(result.get().code()).isEqualTo(code);
                softAssertions.assertThat(result.get().verified()).isFalse();
            });
        }

        @DisplayName("인증번호 발급 시 동일한 번호의 기존 코드가 갱신된다.")
        @Test
        void overwriteExistingVerification() {
            // given
            String phoneNumber = "010-1234-5678";
            Phone phone = new Phone(phoneNumber);
            phoneVerificationRepository.save(phoneNumber, "111111", 180);

            // when
            String newCode = phoneVerificationService.createPhoneVerification(phone);

            // then
            Optional<PhoneVerificationData> result = phoneVerificationRepository.findByPhone(phoneNumber);
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(result).isPresent();
                softAssertions.assertThat(result.get().code()).isEqualTo(newCode);
                softAssertions.assertThat(result.get().verified()).isFalse();
            });
        }
    }

    @DisplayName("전화번호 인증번호 확인")
    @Nested
    class VerifyCode {

        @DisplayName("전화번호 인증번호가 올바르면 예외가 발생하지 않는다.")
        @Test
        void validVerificationCode() {
            // given
            String phoneNumber = "010-1234-5678";
            String code = "123456";
            phoneVerificationRepository.save(phoneNumber, code, 180);
            VerificationCodeRequest request = new VerificationCodeRequest(phoneNumber, code);

            // when & then
            Assertions.assertThatCode(() -> phoneVerificationService.verifyCode(request))
                    .doesNotThrowAnyException();
        }

        @DisplayName("전화번호 인증번호가 올바르지 않으면 예외가 발생한다.")
        @Test
        void invalidVerificationCode() {
            // given
            String phoneNumber = "010-1234-5678";
            phoneVerificationRepository.save(phoneNumber, "123456", 180);
            VerificationCodeRequest request = new VerificationCodeRequest(phoneNumber, "invalidCode");

            // when & then
            Assertions.assertThatThrownBy(() -> phoneVerificationService.verifyCode(request))
                    .isInstanceOf(InvalidPhoneVerificationException.class)
                    .hasMessage(BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage());
        }

        @DisplayName("인증 시도 횟수가 5회를 초과하면 예외가 발생하고 인증 데이터가 삭제된다.")
        @Test
        void exceedMaxAttempts() {
            // given
            String phoneNumber = "010-1234-5678";
            phoneVerificationRepository.save(phoneNumber, "123456", 180);
            VerificationCodeRequest wrongRequest = new VerificationCodeRequest(phoneNumber, "wrongCode");

            for (int i = 0; i < 5; i++) {
                try {
                    phoneVerificationService.verifyCode(wrongRequest);
                } catch (InvalidPhoneVerificationException ignored) {
                }
            }

            // when & then (6번째 시도)
            Assertions.assertThatThrownBy(() -> phoneVerificationService.verifyCode(wrongRequest))
                    .isInstanceOf(InvalidPhoneVerificationException.class);

            Assertions.assertThat(phoneVerificationRepository.findByPhone(phoneNumber)).isEmpty();
        }
    }
}
