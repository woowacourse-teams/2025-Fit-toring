import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MentoringList({ children }: PropsWithChildren) {
  return <StyledContainer>{children}</StyledContainer>;
}

export default MentoringList;

const StyledContainer = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  padding: 1.5rem 2rem;
`;
