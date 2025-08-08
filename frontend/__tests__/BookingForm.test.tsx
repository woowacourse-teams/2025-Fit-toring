import { ThemeProvider } from '@emotion/react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';

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
  describe('필수 입력 미입력 검증', () => {
    it('전화번호 제공 동의를 체크하지 않고 "예약하기" 버튼을 누를 경우 에러메시지가 나타난다.', async () => {
      const mockSubmit = vi.fn();

      renderBookingForm(mockSubmit);

      const submitButton = screen.getByText('예약하기');
      await userEvent.click(submitButton);

      const errorMessage = screen.getByText('전화번호 제공 동의를 해주세요.');

      expect(errorMessage).toBeInTheDocument();
    });
  });
});
