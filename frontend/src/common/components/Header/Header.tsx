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

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
