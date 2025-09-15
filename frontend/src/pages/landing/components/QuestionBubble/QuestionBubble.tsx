import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

interface QuestionBubbleProps {
  direction: 'left' | 'right';
}
function QuestionBubble({
  direction,
  children,
}: PropsWithChildren<QuestionBubbleProps>) {
  return <StyledBubble direction={direction}>{children}</StyledBubble>;
}

export default QuestionBubble;

const StyledBubble = styled.div<{ direction: 'left' | 'right' }>`
  position: relative;
  background-color: white;
  border: 1px solid #e3e3e3;
  border-radius: 50px;
  color: black;
  font-size: 12px;
  font-weight: 500;
  padding: 1rem 3.5rem;
  width: fit-content;
  z-index: 100;

  &::before {
    content: '';
    position: absolute;
    bottom: -8px;
    ${({ direction }) =>
      direction === 'right'
        ? `
          right: 10px;
          border-left: 6.5px solid transparent;
          border-right: 5px solid transparent;
          border-top: 8px solid #e3e3e3;
        `
        : `
          left: 10px;
          border-left: 5px solid transparent;
          border-right: 6.5px solid transparent;
          border-top: 8px solid #e3e3e3;
        `}
    z-index: 0;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -6px;
    ${({ direction }) =>
      direction === 'right'
        ? `
          right: 10px;
          border-left: 6.5px solid transparent;
          border-right: 5px solid transparent;
          border-top: 8px solid white;
        `
        : `
          left: 10px;
          border-left: 5px solid transparent;
          border-right: 6.5px solid transparent;
          border-top: 8px solid white;
        `}
    z-index: 1;
  }
`;
