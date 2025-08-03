import { useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import menuIcon from '../../../../common/assets/images/menuBar.svg';

const MENU_ITEMS = [
  { name: '개설한 멘토링', path: 'created-mentoring' },
  { name: '참여한 멘토링', path: 'participated-mentoring' },
  { name: '회원 정보', path: 'my-profile' },
  { name: '로그아웃', path: 'logout' },
] as const;

function MenuDropDown() {
  const [opened, setOpened] = useState(false);

  const handleMenuButtonClick = () => {
    setOpened((prev) => !prev);
  };

  const [selectedMenu, setSelectedMenu] = useState<
    (typeof MENU_ITEMS)[number]['name']
  >(MENU_ITEMS[0].name);

  const handleSelectMenu = (item: (typeof MENU_ITEMS)[number]) => {
    setSelectedMenu(item.name);
    setOpened((prev) => !prev);
    navigate(item.path);
  };

  const navigate = useNavigate();

  return (
    <StyledContainer>
      <StyledMenuButton onClick={handleMenuButtonClick}>
        <StyledMenuIcon src={menuIcon} alt="메뉴 열기 아이콘" />
      </StyledMenuButton>

      <StyledMenuList opened={opened}>
        {MENU_ITEMS.map((item) => (
          <StyledMenuItem
            key={item.name}
            onClick={() => handleSelectMenu(item)}
            selected={selectedMenu === item.name}
          >
            {item.name}
          </StyledMenuItem>
        ))}
      </StyledMenuList>
    </StyledContainer>
  );
}

export default MenuDropDown;

const StyledContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;

  width: 5rem;
`;

const StyledMenuButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 3.8rem;
  height: 3.8rem;
  padding: 0;
  border: none;

  background: transparent;

  color: ${({ theme }) => theme.FONT.B03};
  cursor: pointer;
  transition: all 0.2s ease;
`;

const StyledMenuIcon = styled.img`
  width: 2.4rem;
`;

const StyledMenuList = styled.ul<{ opened: boolean }>`
  visibility: ${({ opened }) => (opened ? 'visible' : 'hidden')};
  position: absolute;
  top: 100%;
  right: 1rem;
  z-index: 50;

  width: 20rem;
  margin-top: 0.4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 0.4rem 1.6rem rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
  opacity: ${({ opened }) => (opened ? 1 : 0)};
  transform: ${({ opened }) =>
    opened ? 'translateY(0)' : 'translateY(-1rem)'};
  transition: all 0.2s ease;
`;

const StyledMenuItem = styled.li<{ selected: boolean }>`
  width: 100%;
  padding: 1rem 1.2rem;

  background-color: ${({ selected, theme }) =>
    selected ? theme.SYSTEM.MAIN50 : 'transparent'};

  color: ${({ selected, theme }) =>
    selected ? theme.SYSTEM.MAIN700 : theme.FONT.B03};

  transition: all 0.2s ease;
  cursor: pointer;

  :first-of-type {
    border-radius: 16px 16px 0 0;
  }

  :last-of-type {
    border-radius: 0 0 16px 16px;
  }

  &:hover {
    background-color: ${({ theme }) => theme.SYSTEM.MAIN50};

    color: ${({ theme }) => theme.SYSTEM.MAIN700};
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
