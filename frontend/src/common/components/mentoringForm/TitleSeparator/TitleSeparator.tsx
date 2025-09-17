import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function TitleSeparator({ children }: PropsWithChildren) {
  return (
    <>
      <S_Title>{children}</S_Title>
      <S_Wrapper>
        <S_LeftBar />
        <S_RightBar />
      </S_Wrapper>
    </>
  );
}

export default TitleSeparator;

const S_Title = styled.h2`
  margin-bottom: 1.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.H2_R};
  color: ${({ theme }) => theme.FONT.B01};
`;

const S_Wrapper = styled.div`
  display: flex;

  margin-bottom: 2rem;
`;

const S_LeftBar = styled.div`
  width: 6rem;
  height: 0.2rem;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;

const S_RightBar = styled.div`
  width: 100%;
  height: 0.2rem;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN100};
`;
