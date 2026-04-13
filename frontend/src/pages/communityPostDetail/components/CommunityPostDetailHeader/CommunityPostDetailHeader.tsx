import { useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import menuDotsIcon from '../../../../common/assets/images/menuDots.svg';
import Header from '../../../../common/components/Header/Header';
import useOutsideClickRef from '../../../../common/hooks/useOutsideClickRef';

interface CommunityPostDetailHeaderProps {
  showActionButton: boolean;
  onEditClick: () => void;
  onDeleteClick: () => void;
}

function CommunityPostDetailHeader({
  showActionButton,
  onEditClick,
  onDeleteClick,
}: CommunityPostDetailHeaderProps) {
  const navigate = useNavigate();
  const [menuOpened, setMenuOpened] = useState(false);

  const closeMenu = () => {
    setMenuOpened(false);
  };

  const { ref: containerRef } = useOutsideClickRef<HTMLDivElement>(closeMenu);

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  const handleMenuButtonClick = () => {
    setMenuOpened((prev) => !prev);
  };

  const handleEditClick = () => {
    closeMenu();
    onEditClick();
  };

  const handleDeleteClick = () => {
    closeMenu();
    onDeleteClick();
  };

  return (
    <Header>
      <S_Wrapper>
        <S_BackButton type="button" onClick={handleBackButtonClick}>
          <S_BackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </S_BackButton>
        <S_Title>커뮤니티</S_Title>
        {showActionButton && (
          <S_ActionContainer ref={containerRef}>
            <S_MenuButton
              type="button"
              aria-haspopup="menu"
              aria-expanded={menuOpened}
              aria-label="게시글 관리 메뉴 열기"
              onClick={handleMenuButtonClick}
            >
              <S_MenuIcon src={menuDotsIcon} alt="" />
            </S_MenuButton>
            <S_MenuList opened={menuOpened} role="menu">
              <S_MenuItem role="none">
                <S_MenuActionButton
                  type="button"
                  role="menuitem"
                  onClick={handleEditClick}
                >
                  수정
                </S_MenuActionButton>
              </S_MenuItem>
              <S_MenuItem role="none">
                <S_MenuActionButton
                  type="button"
                  role="menuitem"
                  onClick={handleDeleteClick}
                >
                  삭제
                </S_MenuActionButton>
              </S_MenuItem>
            </S_MenuList>
          </S_ActionContainer>
        )}
      </S_Wrapper>
    </Header>
  );
}

export default CommunityPostDetailHeader;

const S_Wrapper = styled.div`
  display: flex;
  align-items: center;
  position: relative;

  height: 100%;
  padding: 1.4rem 1.1rem;
`;

const S_BackButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_BackIcon = styled.img`
  width: 3.4rem;
`;

const S_Title = styled.h1`
  position: absolute;
  top: 50%;
  left: 50%;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  transform: translate(-50%, -50%);
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;

const S_ActionContainer = styled.div`
  position: absolute;
  top: 50%;
  right: 1.1rem;

  transform: translateY(-50%);
`;

const S_MenuButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_MenuIcon = styled.img`
  width: 2.4rem;
  height: 2.4rem;
`;

const S_MenuList = styled.ul<{ opened: boolean }>`
  visibility: ${({ opened }) => (opened ? 'visible' : 'hidden')};
  position: absolute;
  top: calc(100% + 0.8rem);
  right: 0;
  z-index: 10;

  width: 12rem;
  padding: 0.6rem 0;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;
  box-shadow: 0 4px 16px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
  opacity: ${({ opened }) => (opened ? 1 : 0)};
  transform: ${({ opened }) =>
    opened ? 'translateY(0)' : 'translateY(-0.8rem)'};
  transition: opacity 0.2s ease, transform 0.2s ease, visibility 0.2s ease;
`;

const S_MenuItem = styled.li`
  list-style: none;
`;

const S_MenuActionButton = styled.button`
  width: 100%;
  padding: 1rem 1.4rem;
  border: none;

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: left;
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  cursor: pointer;

  &:hover {
    background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
  }
`;
