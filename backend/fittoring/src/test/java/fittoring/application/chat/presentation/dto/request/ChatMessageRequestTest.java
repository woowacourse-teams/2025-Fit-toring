package fittoring.application.chat.presentation.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.lang.reflect.RecordComponent;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ChatMessageRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("TEXT 메시지 요청은")
    class TextMessage {

        @DisplayName("content와 tempId만 가진다.")
        @Test
        void fields() {
            // when
            RecordComponent[] components = ChatTextMessageRequest.class.getRecordComponents();

            // then
            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .containsExactly("content", "tempId");
        }

        @DisplayName("공통 채팅 메시지 요청 타입이다.")
        @Test
        void implementsChatMessageRequest() {
            // then
            assertThat(ChatMessageRequest.class).isAssignableFrom(ChatTextMessageRequest.class);
        }

        @DisplayName("content가 있고 tempId가 있으면 유효하다.")
        @Test
        void validWhenContentAndTempIdExist() {
            // given
            ChatTextMessageRequest request = new ChatTextMessageRequest("안녕하세요", 1L);

            // when
            Set<ConstraintViolation<ChatTextMessageRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @DisplayName("content가 비어 있으면 유효하지 않다.")
        @Test
        void invalidWhenContentIsBlank() {
            // given
            ChatTextMessageRequest request = new ChatTextMessageRequest(" ", 1L);

            // when
            Set<ConstraintViolation<ChatTextMessageRequest>> violations = validator.validate(request);

            // then
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains("메시지 내용은 필수입니다.");
        }

        @DisplayName("content가 2000자를 초과하면 유효하지 않다.")
        @Test
        void invalidWhenContentIsTooLong() {
            // given
            ChatTextMessageRequest request = new ChatTextMessageRequest("a".repeat(2001), 1L);

            // when
            Set<ConstraintViolation<ChatTextMessageRequest>> violations = validator.validate(request);

            // then
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains("메시지는 2000자 이하로 입력해야합니다.");
        }

        @DisplayName("tempId가 없으면 유효하지 않다.")
        @Test
        void invalidWhenTempIdIsNull() {
            // given
            ChatTextMessageRequest request = new ChatTextMessageRequest("안녕하세요", null);

            // when
            Set<ConstraintViolation<ChatTextMessageRequest>> violations = validator.validate(request);

            // then
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains("임시 ID는 필수 입력값입니다.");
        }
    }

    @Nested
    @DisplayName("IMAGE 메시지 요청은")
    class ImageMessage {

        @DisplayName("uploadId와 tempId만 가진다.")
        @Test
        void fields() {
            // when
            RecordComponent[] components = ChatImageMessageRequest.class.getRecordComponents();

            // then
            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .containsExactly("uploadId", "tempId");
        }

        @DisplayName("공통 채팅 메시지 요청 타입이다.")
        @Test
        void implementsChatMessageRequest() {
            // then
            assertThat(ChatMessageRequest.class).isAssignableFrom(ChatImageMessageRequest.class);
        }

        @DisplayName("uploadId와 tempId가 있으면 유효하다.")
        @Test
        void validWhenUploadIdAndTempIdExist() {
            // given
            ChatImageMessageRequest request = new ChatImageMessageRequest("upload-id", 1L);

            // when
            Set<ConstraintViolation<ChatImageMessageRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @DisplayName("uploadId가 비어 있으면 유효하지 않다.")
        @Test
        void invalidWhenUploadIdIsBlank() {
            // given
            ChatImageMessageRequest request = new ChatImageMessageRequest(" ", 1L);

            // when
            Set<ConstraintViolation<ChatImageMessageRequest>> violations = validator.validate(request);

            // then
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains("업로드 ID는 필수입니다.");
        }

        @DisplayName("tempId가 없으면 유효하지 않다.")
        @Test
        void invalidWhenTempIdIsNull() {
            // given
            ChatImageMessageRequest request = new ChatImageMessageRequest("upload-id", null);

            // when
            Set<ConstraintViolation<ChatImageMessageRequest>> violations = validator.validate(request);

            // then
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains("임시 ID는 필수 입력값입니다.");
        }
    }
}
