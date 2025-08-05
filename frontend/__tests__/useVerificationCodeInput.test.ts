import { renderHook, act } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { ERROR_MESSAGE } from '../src/pages/signup/constants/errorMessage';
import useVerificationCodeInput, {
  VERIFICATION_CODE_LENGTH,
} from '../src/pages/signup/hooks/useVerificationCodeInput';

const createFakeChangeEvent = (
  value: string,
): React.ChangeEvent<HTMLInputElement> =>
  ({
    target: { value },
  }) as React.ChangeEvent<HTMLInputElement>;

describe('인증번호 입력 유효성 검사', () => {
  it('숫자가 아닌 문자를 입력하면 값이 변경되지 않는다.', () => {
    const { result } = renderHook(() => useVerificationCodeInput());

    act(() => {
      result.current.handleVerificationCodeChange(createFakeChangeEvent('abc'));
    });

    expect(result.current.verificationCode).toBe('');
  });
});

describe('인증번호 길이 유효성 검사', () => {
  it(`길이가 ${VERIFICATION_CODE_LENGTH}자 미만이면 에러 메시지를 반환한다.`, () => {
    const { result } = renderHook(() => useVerificationCodeInput());

    act(() => {
      result.current.handleVerificationCodeChange(
        createFakeChangeEvent('1'.repeat(VERIFICATION_CODE_LENGTH - 1)),
      );
    });

    expect(result.current.errorMessage).toBe(
      ERROR_MESSAGE.INVALID_PASSWORD_LENGTH,
    );
  });

  it(`길이가 ${VERIFICATION_CODE_LENGTH}자면 에러 메시지를 반환하지 않는다.`, () => {
    const { result } = renderHook(() => useVerificationCodeInput());

    act(() => {
      result.current.handleVerificationCodeChange(
        createFakeChangeEvent('1'.repeat(VERIFICATION_CODE_LENGTH)),
      );
    });

    expect(result.current.errorMessage).toBe('');
  });
});
