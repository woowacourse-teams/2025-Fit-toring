import { describe, it, expect } from 'vitest';

import { ERROR_MESSAGE } from '../src/pages/booking/constants/errorMessage';
import { getPhoneNumberErrorMessage } from '../src/pages/booking/utils/phoneNumberValidator';

describe('getPhoneNumberErrorMessage 유틸 함수 테스트', () => {
  it.each([['0'], ['010-1234'], ['010-1234-567']])(
    '숫자 3자리 이하 입력 시 그대로 반환',
    (phoneNumber) => {
      expect(getPhoneNumberErrorMessage(phoneNumber)).toBe(
        ERROR_MESSAGE.INVALID_PHONE_NUMBER_LENGTH,
      );
    },
  );
});
