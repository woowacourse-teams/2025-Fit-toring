import { describe, it, expect } from 'vitest';

import { getFormattedPhoneNumber } from '../src/pages/booking/utils/getFormattedPhoneNumber ';

describe('getFormattedPhoneNumber 유틸 함수 테스트', () => {
  it('숫자 3자리 미만 입력 시 그대로 반환', () => {
    expect(getFormattedPhoneNumber('')).toBe('');
    expect(getFormattedPhoneNumber('0')).toBe('0');
    expect(getFormattedPhoneNumber('01')).toBe('01');
    expect(getFormattedPhoneNumber('012')).toBe('012');
  });

  it('4~7자리 숫자 입력 시 하이픈 하나 추가', () => {
    expect(getFormattedPhoneNumber('0123')).toBe('012-3');
    expect(getFormattedPhoneNumber('01234')).toBe('012-34');
    expect(getFormattedPhoneNumber('0123456')).toBe('012-3456');
    expect(getFormattedPhoneNumber('01234567')).toBe('012-3456-7');
  });

  it('8자리 이상 숫자 입력 시 하이픈 두 개 추가', () => {
    expect(getFormattedPhoneNumber('012345678')).toBe('012-3456-78');
    expect(getFormattedPhoneNumber('0123456789')).toBe('012-3456-789');
    expect(getFormattedPhoneNumber('01234567890')).toBe('012-3456-7890');
    expect(getFormattedPhoneNumber('012345678901234')).toBe('012-3456-7890');
  });

  it('입력에 숫자가 아닌 문자 포함 시 숫자만 필터링', () => {
    expect(getFormattedPhoneNumber('01a2b3c4d')).toBe('012-34');
    expect(getFormattedPhoneNumber('abc01234567def')).toBe('012-3456-7');
    expect(getFormattedPhoneNumber('!@#0123456789$%^')).toBe('012-3456-789');
  });
});
