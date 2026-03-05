import styled from '@emotion/styled';

import warningIcon from '../../../../common/assets/images/warningIcon.svg';
import { formatToKoreanTime } from '../../../../common/utils/formatToKoreanTime';

interface ChatImageBubbleProps {
  content: string;
  createdAt: string;
  authored: boolean;
  status?: 'success' | 'fail' | 'pending';
}

function ChatImageBubble({
  content,
  createdAt,
  authored,
  status,
}: ChatImageBubbleProps) {
  return (
    <S_Container authored={authored}>
      <S_BubbleWrapper authored={authored}>
        <S_Bubble authored={authored}>
          <S_Image authored={authored} src={content} />
        </S_Bubble>
        <S_Temp authored={authored}>
          {status === 'fail' ? (
            <S_RetryInfoWrapper>
              <S_RetryIcon src={warningIcon} />
              <S_RetryText>전송실패</S_RetryText>
            </S_RetryInfoWrapper>
          ) : null}
          <S_Time>{formatToKoreanTime(createdAt)}</S_Time>
        </S_Temp>
      </S_BubbleWrapper>
    </S_Container>
  );
}

export default ChatImageBubble;

const S_Container = styled.div<Pick<ChatImageBubbleProps, 'authored'>>`
  display: flex;
  flex-direction: column;
  align-items: ${({ authored }) => (authored ? 'end' : 'start')};
  gap: 1rem;
`;

const S_RetryInfoWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.3rem;
`;

const S_RetryText = styled.span`
  color: ${({ theme }) => theme.FONT.ERROR};

  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
`;

const S_RetryIcon = styled.img`
  width: 1.3rem;
  height: 1.3rem;
`;

const S_BubbleWrapper = styled.div<Pick<ChatImageBubbleProps, 'authored'>>`
  display: flex;
  flex-direction: ${({ authored }) => (authored ? 'row-reverse' : 'row')};
  align-items: flex-end;
  gap: 0.5rem;
`;

const S_Bubble = styled.div<Pick<ChatImageBubbleProps, 'authored'>>`
  width: fit-content;
  max-width: 28rem;
  padding: 0.7rem 1.2rem;
  border-radius: ${({ authored }) =>
    authored ? '14px 14px 8px 14px;' : '14px 14px 14px 8px'};

  background-color: ${({ theme, authored }) =>
    authored ? theme.SYSTEM.GRAY900 : theme.SYSTEM.GRAY50};
`;

const S_Temp = styled.div<Pick<ChatImageBubbleProps, 'authored'>>`
  display: flex;
  flex-direction: column;
  align-items: ${({ authored }) => (authored ? 'flex-end' : 'flex-start')};
`;

const S_Image = styled.img<Pick<ChatImageBubbleProps, 'authored'>>`
  height: 15rem;
  max-width: 15rem;

  object-fit: cover;
`;

const S_Time = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C5_R};
  color: ${({ theme }) => theme.SYSTEM.GRAY400};
`;
