interface memberSummary {
  name: string;
  phoneNumber: string;
}

export const MEMBER_SUMMARY: memberSummary = {
  name: '홍길동',
  phoneNumber: '010-1234-9048',
} as const;
