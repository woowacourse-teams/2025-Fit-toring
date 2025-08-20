import { useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { postLogout } from '../../../../common/apis/postLogout';
import menuIcon from '../../../../common/assets/images/menuBar.svg';
import { useAuth } from '../../../../common/components/AuthProvider/AuthProvider';
import { PAGE_URL } from '../../../../common/constants/url';

type MenuItemName =
  | '개설한 멘토링'
  | '참여한 멘토링'
  | '회원 정보'
  | '로그아웃';

interface MenuItem {
  name: MenuItemName;
  action: () => Promise<void> | void;
}

function MenuDropDown() {
  const [opened, setOpened] = useState(false);

  const handleMenuButtonClick = () => {
    setOpened((prev) => !prev);
  };

  const MENU_ITEMS: MenuItem[] = [
    {
      name: '개설한 멘토링',
      action: () => navigate(PAGE_URL.CREATED_MENTORING),
    },
    {
      name: '참여한 멘토링',
      action: () => navigate(PAGE_URL.PARTICIPATED_MENTORING),
    },
    // { name: '회원 정보', path: 'my-profile' },
    { name: '로그아웃', action: async () => await handleLogout(PAGE_URL.HOME) },
  ];

  const [selectedMenu, setSelectedMenu] =
    useState<MenuItemName>('개설한 멘토링');

  const navigate = useNavigate();

  const { logout } = useAuth();

  const handleSelectMenu = async (item: MenuItem) => {
    setSelectedMenu(item.name);
    setOpened((prev) => !prev);
    await item.action();
  };

  const handleLogout = async (url: string) => {
    try {
      await postLogout();
      logout();
      navigate(url);
    } catch (error) {
      console.error('Logout failed', error);
    }
  };

  return (
    <StyledContainer>
      <StyledMenuButton onClick={handleMenuButtonClick}>
        <StyledMenuIcon src={menuIcon} alt="메뉴 열기 아이콘" />
      </StyledMenuButton>

      <StyledMenuList opened={opened}>
        {MENU_ITEMS.map((item) => (
          <StyledMenuItem
            key={item.name}
            onClick={async () => await handleSelectMenu(item)}
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
`;

const StyledMenuButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 0;
  border: none;

  background: transparent;

  color: ${({ theme }) => theme.FONT.B03};
  cursor: pointer;
  transition: all 0.2s ease;
`;

const StyledMenuIcon = styled.img`
  width: 2.4rem;
  height: 2.4rem;
  aspect-ratio: 1 / 1;
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
  box-shadow: 0 4px 16px rgb(0 0 0 / 10%);

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
