import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MentorCardList({ children }: PropsWithChildren) {
  return <StyledContainer>{children}</StyledContainer>;
}

export default MentorCardList;

const StyledContainer = styled.ul`
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 3rem;

  width: 100%;
  padding: 1rem 1.4rem;
`;
