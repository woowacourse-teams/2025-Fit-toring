import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate, useNavigationType } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/chevron-left.svg';
import { PAGE_URL } from '../../../../common/constants/url';

function DetailHeader() {
  const navigate = useNavigate();
  const navigationType = useNavigationType();
  const [hasScrolled, setHasScrolled] = useState(false);

  const handleMoveBack = () => {
    if (navigationType === 'POP') {
      navigate(PAGE_URL.HOME, { replace: true });
    } else {
      navigate(-1);
    }
  };

  useEffect(() => {
    const handleScroll = () => {
      setHasScrolled(window.scrollY > 0);
    };

    handleScroll();
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <S_Container hasScrolled={hasScrolled}>
      <S_HeaderWrapper>
        <S_BackButton onClick={handleMoveBack} aria-label="이전 페이지로 이동">
          <S_Img
            src={backIcon}
            alt=""
            aria-hidden="true"
            hasScrolled={hasScrolled}
          />
        </S_BackButton>
        <S_Title hasScrolled={hasScrolled}>상세 정보</S_Title>
      </S_HeaderWrapper>
    </S_Container>
  );
}

export default DetailHeader;

const S_Container = styled.header<{ hasScrolled: boolean }>`
  position: fixed;
  top: 0;
  z-index: 100;

  width: 48rem;
  height: 5.7rem;
  border-bottom: ${({ hasScrolled, theme }) =>
    hasScrolled ? `1px solid ${theme.OUTLINE.REGULAR}` : 'none'};

  background: ${({ hasScrolled, theme }) =>
    hasScrolled ? theme.BG.WHITE : 'transparent'};

  transition:
    background-color 0.2s ease,
    border-color 0.2s ease;

  @media screen and (width >= 481px) {
    left: 50%;
    transform: translateX(-50%);
  }

  @media screen and (width <= 480px) {
    width: 100%;
  }
`;

const S_HeaderWrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
`;

const S_BackButton = styled.button`
  position: absolute;

  margin-left: 1rem;
  padding: 0.8rem;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_Img = styled.img<{ hasScrolled: boolean }>`
  width: 3rem;
  height: 3rem;

  filter: ${({ hasScrolled }) => (hasScrolled ? 'brightness(0)' : 'none')};
  transition: filter 0.2s ease;
`;

const S_Title = styled.h3<{ hasScrolled: boolean }>`
  flex-grow: 1;

  color: ${({ hasScrolled, theme }) =>
    hasScrolled ? theme.FONT.B01 : 'transparent'};
  text-align: center;
  transition: color 0.2s ease;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;
