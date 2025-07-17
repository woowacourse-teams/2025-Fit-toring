import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function Footer({ children }: PropsWithChildren) {
  return <StyledContainer>{children}</StyledContainer>;
}

export default Footer;

const StyledContainer = styled.footer`
  width: 100%;
  height: 6rem;
`;
