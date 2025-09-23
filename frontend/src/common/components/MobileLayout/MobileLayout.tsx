import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function MobileLayout({ children }: PropsWithChildren) {
  return (
    <S_Container>
      <S_Contents>{children}</S_Contents>
    </S_Container>
  );
}

export default MobileLayout;

const S_Container = styled.main`
  display: flex;
  justify-content: center;

  width: 100%;
  height: fit-content;
  min-height: 100%;
`;

const S_Contents = styled.section`
  width: 48rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};

  background-color: ${({ theme }) => theme.BG.WHITE};

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
