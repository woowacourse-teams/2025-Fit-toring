import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MobileLayout({ children }: PropsWithChildren) {
  return (
    <StyledContainer>
      <StyledContents>{children}</StyledContents>
    </StyledContainer>
  );
}

export default MobileLayout;

const StyledContainer = styled.main`
  display: flex;
  justify-content: center;

  width: 100%;
  height: fit-content;
`;

const StyledContents = styled.section`
  width: 48rem;

  background-color: ${({ theme }) => theme.BG.LIGHT};

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
