import styled from '@emotion/styled';

interface ChatBubbleProps {
  text: string;
  createdAt: string;
  authored: boolean;
}

function ChatBubble({ text, createdAt, authored }: ChatBubbleProps) {
  return (
    <S_Contaienr authored={authored}>
      <S_Bubble authored={authored}>
        <S_Text authored={authored}>{text}</S_Text>
      </S_Bubble>
      <S_Time>{createdAt}</S_Time>
    </S_Contaienr>
  );
}

export default ChatBubble;

const S_Contaienr = styled.div<Pick<ChatBubbleProps, 'authored'>>`
  display: flex;
  flex-direction: column;
  align-items: ${({ authored }) => (authored ? 'flex-end' : 'flex-start')};
  gap: 0.2rem;
`;

const S_Bubble = styled.div<Pick<ChatBubbleProps, 'authored'>>`
  width: fit-content;
  max-width: 30rem;
  padding: 0.7rem 1.2rem;
  border-radius: ${({ authored }) =>
    authored ? '14px 14px 8px 14px;' : '14px 14px 14px 8px'};

  background-color: ${({ theme, authored }) =>
    authored ? theme.SYSTEM.GRAY900 : theme.SYSTEM.GRAY50};
`;

const S_Text = styled.p<Pick<ChatBubbleProps, 'authored'>>`
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  color: ${({ theme, authored }) =>
    authored ? theme.BG.WHITE : theme.FONT.B01};
`;

const S_Time = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
  color: ${({ theme }) => theme.SYSTEM.GRAY400};
`;
