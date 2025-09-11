import { useEffect, useState, type PropsWithChildren } from 'react';
import styled from '@emotion/styled';

function Header({ children }: PropsWithChildren) {
  const [hasScrolled, setHasScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setHasScrolled(window.scrollY > 0); // 스크롤이 0보다 크면 true
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <StyledContainer hasScrolled={hasScrolled}>{children}</StyledContainer>
  );
}

export default Header;

const StyledContainer = styled.header<{ hasScrolled: boolean }>`
  position: sticky;
  top: 0;
  z-index: 100;

  width: 100%;
  height: 5.7rem;
  background: ${({ theme }) => theme.BG.WHITE};

  border-bottom: ${({ hasScrolled, theme }) =>
    hasScrolled ? `1px solid ${theme.OUTLINE.REGULAR}` : 'none'};

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
