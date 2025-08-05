import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function TitleSeparator({ children }: PropsWithChildren) {
  return (
    <>
      <StyledTitle>{children}</StyledTitle>
      <StyledWrapper>
        <StyledLeftBar />
        <StyledRightBar />
      </StyledWrapper>
    </>
  );
}

export default TitleSeparator;

const StyledTitle = styled.h2`
  margin-bottom: 1.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.H2_R};
  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledWrapper = styled.div`
  display: flex;

  margin-bottom: 2rem;
`;

const StyledLeftBar = styled.div`
  width: 6rem;
  height: 0.2rem;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;

const StyledRightBar = styled.div`
  width: 100%;
  height: 0.2rem;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN100};
`;
