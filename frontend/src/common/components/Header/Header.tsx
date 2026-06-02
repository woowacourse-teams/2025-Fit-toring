import { useEffect, useState, type PropsWithChildren } from 'react';

import styled from '@emotion/styled';

interface HeaderProps extends PropsWithChildren {
  overlay?: boolean;
}

function Header({ children, overlay = false }: HeaderProps) {
  const [hasScrolled, setHasScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setHasScrolled(window.scrollY > 0);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <S_Container hasScrolled={hasScrolled} overlay={overlay}>
      {children}
    </S_Container>
  );
}

export default Header;

const S_Container = styled.header<{ hasScrolled: boolean; overlay: boolean }>`
  position: ${({ overlay }) => (overlay ? 'fixed' : 'sticky')};
  top: 0;
  left: ${({ overlay }) => (overlay ? '50%' : 'auto')};
  transform: ${({ overlay }) => (overlay ? 'translateX(-50%)' : 'none')};

  z-index: 100;

  width: 100%;
  height: ${({ overlay }) =>
    overlay ? 'calc(5.7rem + env(safe-area-inset-top))' : '5.7rem'};
  max-width: ${({ overlay }) => (overlay ? '48rem' : 'none')};
  padding-top: ${({ overlay }) =>
    overlay ? 'env(safe-area-inset-top)' : '0'};
  border-bottom: ${({ hasScrolled, theme }) =>
    hasScrolled ? `1px solid ${theme.OUTLINE.REGULAR}` : 'none'};

  background: ${({ hasScrolled, overlay, theme }) =>
    overlay && !hasScrolled ? 'transparent' : theme.BG.WHITE};

  @media screen and (width <= 480px) {
    width: 100%;
  }
`;
