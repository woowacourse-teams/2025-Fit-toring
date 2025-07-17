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
  height: 100%;
`;

const StyledContents = styled.section`
  min-width: 39rem;

  border: solid black 0.1rem; // Todo: 삭제해야함
`;
