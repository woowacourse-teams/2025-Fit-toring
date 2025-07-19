import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function Header({ children }: PropsWithChildren) {
  return <StyledContainer>{children}</StyledContainer>;
}

export default Header;

const StyledContainer = styled.header`
  position: fixed;
  width: 48rem;
  height: 5.7rem;
  border-bottom: 1px solid ${({ theme }) => theme.LINE.REGULAR};
  background: ${({ theme }) => theme.BG.LIGHT + 'CC'};
  box-shadow:
    0 1px 3px 0 rgb(0 0 0 / 10%),
    0 1px 2px -1px rgb(0 0 0 / 10%);

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
