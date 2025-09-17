import { useEffect, useState, type PropsWithChildren } from 'react';

import styled from '@emotion/styled';

function Header({ children }: PropsWithChildren) {
  const [hasScrolled, setHasScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setHasScrolled(window.scrollY > 0);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return <S_Container hasScrolled={hasScrolled}>{children}</S_Container>;
}

export default Header;

const S_Container = styled.header<{ hasScrolled: boolean }>`
  position: sticky;
  top: 0;
  z-index: 100;

  width: 100%;
  height: 5.7rem;
  border-bottom: ${({ hasScrolled, theme }) =>
    hasScrolled ? `1px solid ${theme.OUTLINE.REGULAR}` : 'none'};

  background: ${({ theme }) => theme.BG.WHITE};

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
