import styled from '@emotion/styled';

import QuestionBubble from '../QuestionBubble/QuestionBubble';

function FitnessQuestionFlow() {
  return (
    <StyledContainer>
      <StyledQuestionBubbles>
        <StyledWrapper direction="left" padding={8}>
          <QuestionBubble direction="left">
            PT는 너무 부담스러운데...
          </QuestionBubble>
        </StyledWrapper>

        <StyledWrapper direction="right" padding={5}>
          <QuestionBubble direction="right">
            스쿼트만 하면 무릎이 아파요!
          </QuestionBubble>
        </StyledWrapper>

        <StyledWrapper direction="left" padding={3}>
          <QuestionBubble direction="left">
            운동이 처음이라 뭐부터 해야할지 모르겠어요...
          </QuestionBubble>
        </StyledWrapper>

        <StyledWrapper direction="right" padding={3}>
          <QuestionBubble direction="right">
            헬스장에 가지않고 간단하게 상담받고 싶은데..
          </QuestionBubble>
        </StyledWrapper>
      </StyledQuestionBubbles>
      <StyledDots>
        <StyledDot>.</StyledDot>
        <StyledDot>.</StyledDot>
        <StyledDot>.</StyledDot>
      </StyledDots>

      <StyledText>간단하게 질문할 수 있는 곳 없을까?</StyledText>
    </StyledContainer>
  );
}

export default FitnessQuestionFlow;

const StyledContainer = styled.div`
  background: ${({ theme }) => `
  linear-gradient(
    180deg,
    ${theme.SYSTEM.GRAY50} 0%,
    #fff 100%
  )
`};
  padding: 10rem 0;
  display: flex;
  flex-direction: column;
  gap: 3rem;
`;

const StyledQuestionBubbles = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  font-size: 1.5rem;
`;

const StyledWrapper = styled.div<{
  direction: 'left' | 'right';
  padding?: number;
}>`
  display: flex;
  justify-content: ${({ direction }) =>
    direction === 'left' ? 'flex-start' : 'flex-end'};
  padding-left: ${({ direction, padding }) =>
    direction === 'left' ? `${padding ?? 0}rem` : '0'};
  padding-right: ${({ direction, padding }) =>
    direction === 'right' ? `${padding ?? 0}rem` : '0'};
`;

const StyledDots = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  align-items: center;
`;

const StyledDot = styled.p`
  color: ${({ theme }) => theme.SYSTEM.GRAY500};
`;

const StyledText = styled.p`
  text-align: center;
  font-size: 1.8rem;
`;
