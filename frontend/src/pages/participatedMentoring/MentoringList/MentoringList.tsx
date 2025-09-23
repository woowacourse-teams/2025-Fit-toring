import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MentoringList({ children }: PropsWithChildren) {
  return <S_Container>{children}</S_Container>;
}

export default MentoringList;

const S_Container = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;
