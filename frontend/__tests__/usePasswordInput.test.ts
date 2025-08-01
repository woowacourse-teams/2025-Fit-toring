import { renderHook, act } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { ERROR_MESSAGE } from '../src/pages/signup/constants/errorMessage';
import usePasswordInput from '../src/pages/signup/hooks/usePasswordInput';

const createFakeChangeEvent = (
  value: string,
): React.ChangeEvent<HTMLInputElement> => {
  return {
    target: { value },
  } as React.ChangeEvent<HTMLInputElement>;
};

describe('usePasswordInput 훅 테스트', () => {
  describe('비밀번호 길이 유효성 검사', () => {
    it('비밀번호가 5자 미만이면 에러 메시지를 반환한다', () => {
      const { result } = renderHook(() => usePasswordInput());

      act(() => {
        result.current.handlePasswordChange(createFakeChangeEvent('1234'));
      });

      expect(result.current.passwordErrorMessage).toBe(
        ERROR_MESSAGE.INVALID_PASSWORD_LENGTH,
      );
    });

    it('비밀번호가 5자일 때는 유효하다', () => {
      const { result } = renderHook(() => usePasswordInput());

      act(() => {
        result.current.handlePasswordChange(createFakeChangeEvent('12345'));
      });

      expect(result.current.passwordErrorMessage).toBe('');
    });

    it('비밀번호가 20자일 때는 유효하다', () => {
      const { result } = renderHook(() => usePasswordInput());

      act(() => {
        result.current.handlePasswordChange(
          createFakeChangeEvent('a'.repeat(20)),
        );
      });

      expect(result.current.passwordErrorMessage).toBe('');
    });

    it('비밀번호가 21자면 에러 메시지를 반환한다', () => {
      const { result } = renderHook(() => usePasswordInput());

      act(() => {
        result.current.handlePasswordChange(
          createFakeChangeEvent('a'.repeat(21)),
        );
      });

      expect(result.current.passwordErrorMessage).toBe(
        ERROR_MESSAGE.INVALID_PASSWORD_LENGTH,
      );
    });
  });

  describe('비밀번호 확인 불일치 검사', () => {
    it('비밀번호와 비밀번호 확인이 일치하지 않으면 에러 메시지를 반환한다', () => {
      const { result } = renderHook(() => usePasswordInput());

      act(() => {
        result.current.handlePasswordChange(
          createFakeChangeEvent('password123'),
        );

        result.current.handlePasswordConfirmChange(
          createFakeChangeEvent('password321'),
        );
      });

      expect(result.current.passwordConfirmErrorMessage).toBe(
        ERROR_MESSAGE.NOT_MATCH,
      );
    });

    it('비밀번호와 비밀번호 확인이 일치하면 에러 메시지를 반환하지 않는다', () => {
      const { result } = renderHook(() => usePasswordInput());

      act(() => {
        result.current.handlePasswordChange(
          createFakeChangeEvent('password123'),
        );

        result.current.handlePasswordConfirmChange(
          createFakeChangeEvent('password123'),
        );
      });

      expect(result.current.passwordConfirmErrorMessage).toBe('');
    });
  });
});
