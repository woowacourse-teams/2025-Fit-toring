import { css } from '@emotion/react';
import styled from '@emotion/styled';

import timeIcon from '../../../../common/assets/images/timeIcon.svg';
import Button from '../../../../common/components/Button/Button';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';

interface BookingSummarySectionProps {
  price: number;
}

function BookingSummarySection({ price }: BookingSummarySectionProps) {
  return (
    <S_Container>
      <S_Wrapper>
        <TextWithIcon iconSrc={timeIcon} iconName="시간 아이콘" text="15분" />
        <S_Price>{price.toLocaleString()}원</S_Price>
      </S_Wrapper>
      <Button
        customStyle={css`
          flex-grow: 1;

          padding: 0.8rem 0;
        `}
      >
        예약하기
      </Button>
    </S_Container>
  );
}

export default BookingSummarySection;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;

  width: 100%;
  height: 100%;
`;

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.22rem;
`;

const S_Price = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.B1_B};
  font-weight: 600;
`;
