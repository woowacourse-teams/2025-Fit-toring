import styled from '@emotion/styled';

// TODO: API 연동 후 실제 데이터로 교체 필요
const MY_PAGE_STATS = [
  { label: '내가 운영하는 멘토링', value: 3 },
  { label: '내가 듣는 멘토링', value: 5 },
  { label: '작성한 글', value: 12 },
] as const;

function ActivityStatistics() {
  return (
    <S_Stats aria-label="마이페이지 활동 통계">
      {MY_PAGE_STATS.map((stat) => (
        <S_StatItem
          key={stat.label}
          aria-label={`${stat.label} ${stat.value}개`}
        >
          <S_StatLabel>{stat.label}</S_StatLabel>
          <S_StatValue>
            <S_StatNumberText>{` ${stat.value}개`}</S_StatNumberText>
          </S_StatValue>
        </S_StatItem>
      ))}
    </S_Stats>
  );
}

export default ActivityStatistics;

const S_Stats = styled.section`
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));

  width: 100%;
  padding: 2.6rem 0;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_StatItem = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.8rem;

  min-width: 0;

  &:not(:last-of-type) {
    border-right: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  }
`;

const S_StatLabel = styled.span`
  color: ${({ theme }) => theme.SYSTEM.GRAY800};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_StatValue = styled.span`
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 0.3rem;

  color: ${({ theme }) => theme.SYSTEM.MAIN500};
  line-height: 1;
`;

const S_StatNumberText = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.LB3_SB}
`;
