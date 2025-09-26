import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MentorCardList({ children }: PropsWithChildren) {
  return <S_Container>{children}</S_Container>;
}

export default MentorCardList;

const S_Container = styled.ul`
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1.4rem;
  position: relative;

  width: 100%;
  padding: 1rem 1.1rem;
`;
