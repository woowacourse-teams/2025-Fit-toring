import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MentorCardList({ children }: PropsWithChildren) {
  return <StyledContainer>{children}</StyledContainer>;
}

export default MentorCardList;

const StyledContainer = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 3rem;
  justify-content: center;
  padding: 1rem 1.4rem;
`;
