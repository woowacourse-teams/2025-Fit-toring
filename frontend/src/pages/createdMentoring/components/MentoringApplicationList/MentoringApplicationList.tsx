import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MentoringApplicationList({ children }: PropsWithChildren) {
  return <S_Container>{children}</S_Container>;
}

export default MentoringApplicationList;

const S_Container = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
`;
