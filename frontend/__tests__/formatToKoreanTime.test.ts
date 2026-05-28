import { afterEach, describe, expect, it, vi } from 'vitest';

import {
  formatChatDateDivider,
  formatChatRoomListDate,
  formatToKoreanTime,
  isSameLocalDate,
  isSameLocalMinute,
} from '../src/common/utils/formatToKoreanTime';

describe('채팅 날짜 시간 포맷 유틸', () => {
  afterEach(() => {
    vi.useRealTimers();
  });

  it('오전/오후 시간 형식으로 변환한다.', () => {
    expect(formatToKoreanTime('2026-05-28T02:40:00')).toBe('오전 2:40');
    expect(formatToKoreanTime('2026-05-28T14:40:00')).toBe('오후 2:40');
    expect(formatToKoreanTime('2026-05-28T00:05:00')).toBe('오전 12:05');
    expect(formatToKoreanTime('2026-05-28T12:05:00')).toBe('오후 12:05');
  });

  it('채팅방 목록에서 오늘 메시지는 시간으로 표시한다.', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-05-28T18:03:34'));

    expect(formatChatRoomListDate('2026-05-28T14:40:00')).toBe('오후 2:40');
  });

  it('채팅방 목록에서 오늘이 아닌 메시지는 월 일로 표시한다.', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-05-28T18:03:34'));

    expect(formatChatRoomListDate('2026-05-26T14:40:00')).toBe('5월 26일');
  });

  it('날짜 구분선 형식으로 변환한다.', () => {
    expect(formatChatDateDivider('2026-05-28T14:40:00')).toBe(
      '2026년 5월 28일 목요일',
    );
  });

  it('브라우저 로컬 날짜 기준으로 같은 날짜를 비교한다.', () => {
    expect(
      isSameLocalDate('2026-05-28T00:00:00', '2026-05-28T23:59:59'),
    ).toBe(true);
    expect(
      isSameLocalDate('2026-05-28T23:59:59', '2026-05-29T00:00:00'),
    ).toBe(false);
  });

  it('브라우저 로컬 분 기준으로 같은 분을 비교한다.', () => {
    expect(
      isSameLocalMinute('2026-05-28T02:40:00', '2026-05-28T02:40:59'),
    ).toBe(true);
    expect(
      isSameLocalMinute('2026-05-28T02:40:59', '2026-05-28T02:41:00'),
    ).toBe(false);
  });
});
