import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function Footer({ children }: PropsWithChildren) {
  return <S_Container>{children}</S_Container>;
}

export default Footer;

const S_Container = styled.footer`
  width: 100%;
  height: 6rem;
`;
