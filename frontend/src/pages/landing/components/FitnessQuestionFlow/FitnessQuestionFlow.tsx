import styled from '@emotion/styled';

import QuestionBubble from '../QuestionBubble/QuestionBubble';

function FitnessQuestionFlow() {
  return (
    <S_Container>
      <S_ScreenReaderOnly>이런 고민, 있으셨죠?</S_ScreenReaderOnly>

      <S_QuestionBubbles>
        <S_Wrapper direction="left" padding={8}>
          <QuestionBubble direction="left">
            PT는 너무 부담스러운데...
          </QuestionBubble>
        </S_Wrapper>

        <S_Wrapper direction="right" padding={5}>
          <QuestionBubble direction="right">
            스쿼트만 하면 무릎이 아파요!
          </QuestionBubble>
        </S_Wrapper>

        <S_Wrapper direction="left" padding={3}>
          <QuestionBubble direction="left">
            운동이 처음이라 뭐부터 해야할지 모르겠어요...
          </QuestionBubble>
        </S_Wrapper>

        <S_Wrapper direction="right" padding={3}>
          <QuestionBubble direction="right">
            헬스장에 가지않고 간단하게 상담받고 싶은데..
          </QuestionBubble>
        </S_Wrapper>
      </S_QuestionBubbles>
      <S_Dots>
        <S_Dot>.</S_Dot>
        <S_Dot>.</S_Dot>
        <S_Dot>.</S_Dot>
      </S_Dots>

      <S_Text>간단하게 질문할 수 있는 곳 없을까?</S_Text>
    </S_Container>
  );
}

export default FitnessQuestionFlow;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  gap: 3rem;
  position: relative;

  padding: 10rem 0;

  background: ${({ theme }) => `
  linear-gradient(
    180deg,
    ${theme.SYSTEM.GRAY50} 0%,
    #fff 100%
  )
`};
`;

const S_ScreenReaderOnly = styled.h2`
  overflow: hidden;
  position: absolute;
  top: 0;
  left: 0;

  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  border: 0;

  white-space: nowrap;
  clip: rect(0, 0, 0, 0);
`;

const S_QuestionBubbles = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  font-size: 1.5rem;
`;

const S_Wrapper = styled.div<{
  direction: 'left' | 'right';
  padding?: number;
}>`
  display: flex;
  justify-content: ${({ direction }) =>
    direction === 'left' ? 'flex-start' : 'flex-end'};

  padding-right: ${({ direction, padding }) =>
    direction === 'right' ? `${padding ?? 0}rem` : '0'};
  padding-left: ${({ direction, padding }) =>
    direction === 'left' ? `${padding ?? 0}rem` : '0'};
`;

const S_Dots = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.4rem;
`;

const S_Dot = styled.p`
  color: ${({ theme }) => theme.SYSTEM.GRAY500};
`;

const S_Text = styled.p`
  font-size: 1.8rem;
  text-align: center;
`;
