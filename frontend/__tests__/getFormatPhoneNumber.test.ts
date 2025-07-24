import { describe, it, expect } from 'vitest';

import { getFormatPhoneNumber } from '../src/pages/booking/utils/getFormatPhoneNumber';

describe('getFormatPhoneNumber 유틸 함수 테스트', () => {
  it('숫자 3자리 미만 입력 시 그대로 반환', () => {
    expect(getFormatPhoneNumber('')).toBe('');
    expect(getFormatPhoneNumber('0')).toBe('0');
    expect(getFormatPhoneNumber('01')).toBe('01');
    expect(getFormatPhoneNumber('012')).toBe('012');
  });

  it('4~7자리 숫자 입력 시 하이픈 하나 추가', () => {
    expect(getFormatPhoneNumber('0123')).toBe('012-3');
    expect(getFormatPhoneNumber('01234')).toBe('012-34');
    expect(getFormatPhoneNumber('0123456')).toBe('012-3456');
    expect(getFormatPhoneNumber('01234567')).toBe('012-3456-7');
  });

  it('8자리 이상 숫자 입력 시 하이픈 두 개 추가', () => {
    expect(getFormatPhoneNumber('012345678')).toBe('012-3456-78');
    expect(getFormatPhoneNumber('0123456789')).toBe('012-3456-789');
    expect(getFormatPhoneNumber('01234567890')).toBe('012-3456-7890');
    expect(getFormatPhoneNumber('012345678901234')).toBe('012-3456-7890');
  });

  it('입력에 숫자가 아닌 문자 포함 시 숫자만 필터링', () => {
    expect(getFormatPhoneNumber('01a2b3c4d')).toBe('012-34');
    expect(getFormatPhoneNumber('abc01234567def')).toBe('012-3456-7');
    expect(getFormatPhoneNumber('!@#0123456789$%^')).toBe('012-3456-789');
  });
});
