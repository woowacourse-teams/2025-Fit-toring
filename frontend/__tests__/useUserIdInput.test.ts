import { renderHook, act } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { ERROR_MESSAGE } from '../src/pages/signup/constants/errorMessage';
import { ID } from '../src/pages/signup/constants/id';
import useUserIdInput from '../src/pages/signup/hooks/useUserIdInput';

const createFakeChangeEvent = (
  value: string,
): React.ChangeEvent<HTMLInputElement> =>
  ({
    target: { value },
  }) as React.ChangeEvent<HTMLInputElement>;

describe('useUserIdInput 훅 테스트', () => {
  describe('아이디 입력 유효성 검사', () => {
    it('영문자, 숫자 외의 문자가 입력되면 에러 메시지를 반환한다.', () => {
      const { result } = renderHook(() => useUserIdInput());

      act(() => {
        result.current.handleUserIdChange(createFakeChangeEvent('abc123!@#'));
      });

      expect(result.current.errorMessage).toBe(
        ERROR_MESSAGE.INVALID_USER_ID_CHARACTERS,
      );
    });
  });

  describe('아이디 길이 유효성 검사', () => {
    it(`길이가 ${ID.MIN_LENGTH - 1}자 이하면 에러 메시지를 반환한다`, () => {
      const { result } = renderHook(() => useUserIdInput());

      act(() => {
        result.current.handleUserIdChange(
          createFakeChangeEvent('a'.repeat(ID.MIN_LENGTH - 1)),
        );
      });

      expect(result.current.errorMessage).toBe(ERROR_MESSAGE.INVALID_ID_LENGTH);
    });

    it(`길이가 ${ID.MIN_LENGTH}자이면 에러 메시지를 반환하지 않는다`, () => {
      const { result } = renderHook(() => useUserIdInput());

      act(() => {
        result.current.handleUserIdChange(
          createFakeChangeEvent('a'.repeat(ID.MIN_LENGTH)),
        );
      });

      expect(result.current.errorMessage).toBe('');
    });

    it(`길이가 ${ID.MAX_LENGTH}자이면 에러 메시지를 반환하지 않는다`, () => {
      const { result } = renderHook(() => useUserIdInput());

      act(() => {
        result.current.handleUserIdChange(
          createFakeChangeEvent('a'.repeat(ID.MAX_LENGTH)),
        );
      });

      expect(result.current.errorMessage).toBe('');
    });

    it(`길이가 ${ID.MAX_LENGTH + 1}자 이상이면 에러 메시지를 반환한다`, () => {
      const { result } = renderHook(() => useUserIdInput());

      act(() => {
        result.current.handleUserIdChange(
          createFakeChangeEvent('a'.repeat(ID.MAX_LENGTH + 1)),
        );
      });

      expect(result.current.errorMessage).toBe(ERROR_MESSAGE.INVALID_ID_LENGTH);
    });
  });
});
