import { ThemeProvider } from '@emotion/react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';

import { ERROR_MESSAGE } from '../src/common/constants/errorMessage';
import { THEME } from '../src/common/styles/theme';
import BookingForm from '../src/pages/booking/components/BookingForm/BookingForm';

const renderBookingForm = (handleBookingButtonClick: () => void) => {
  return render(
    <ThemeProvider theme={THEME}>
      <BookingForm
        handleBookingButtonClick={handleBookingButtonClick}
        mentoringId={1}
      />
    </ThemeProvider>,
  );
};

describe('BookingForm 검증', () => {
  describe('상담자명 이름 입력 검증', () => {
    it('상담자명 이름이 5글자 이상이면 길이 에러 메시지를 반환한다.', async () => {
      renderBookingForm(() => {});

      const input = screen.getByTestId('mentee-name-input');
      await userEvent.type(input, '홍길동동동동');

      const errorText = screen.getByText(ERROR_MESSAGE.INVALID_NAME_LENGTH);
      expect(errorText).toBeInTheDocument();
    });

    it('상담자명 이름 입력시 한글외의 문자를 입력하면 에러 메시지를 반환한다.', async () => {
      renderBookingForm(() => {});

      const input = screen.getByTestId('mentee-name-input');
      await userEvent.type(input, '홍길동12');

      const errorText = screen.getByText(ERROR_MESSAGE.INVALID_NAME_CHARACTERS);
      expect(errorText).toBeInTheDocument();
    });
  });

  describe('전화번호 입력 검증', () => {
    it('전화번호가 11자 미만이면 에러 메시지를 반환한다.', async () => {
      renderBookingForm(() => {});

      const input = screen.getByTestId('phone-number-input');
      await userEvent.type(input, '010-1234-567');

      const errorText = screen.getByText(
        ERROR_MESSAGE.INVALID_PHONE_NUMBER_LENGTH,
      );
      expect(errorText).toBeInTheDocument();
    });
  });

  describe('필수 입력 미입력 검증', () => {
    it('상담자 이름, 전화번호 모두 미입력시 제출함수가 동작하지 않는다.', async () => {
      const mockSubmit = vi.fn();

      renderBookingForm(mockSubmit);

      const input = screen.getByTestId('phone-number-input');
      await userEvent.type(input, '010-1234-5678');

      const submitButton = screen.getByText('예약하기');
      await userEvent.click(submitButton);

      expect(mockSubmit).not.toHaveBeenCalled();
    });

    it('상담자 이름만 미입력시 제출함수가 동작하지 않는다.', async () => {
      const mockSubmit = vi.fn();

      renderBookingForm(mockSubmit);

      const submitButton = screen.getByText('예약하기');
      await userEvent.click(submitButton);

      expect(mockSubmit).not.toHaveBeenCalled();
    });

    it('전화번호만 미입력시 제출함수가 동작하지 않는다.', async () => {
      const mockSubmit = vi.fn();

      renderBookingForm(mockSubmit);

      const input = screen.getByTestId('mentee-name-input');
      await userEvent.type(input, '홍길동');

      const submitButton = screen.getByText('예약하기');
      await userEvent.click(submitButton);

      expect(mockSubmit).not.toHaveBeenCalled();
    });
  });
});
