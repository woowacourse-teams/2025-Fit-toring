import styled from '@emotion/styled';
import { useLocation, useNavigate } from 'react-router-dom';

import ChatActiveIcon from '../../../common/assets/images/chatActiveIcon.svg';
import ChatIcon from '../../../common/assets/images/chatIcon.svg';
import HomeActiveIcon from '../../../common/assets/images/homeActiveIcon.svg';
import HomeIcon from '../../../common/assets/images/homeIcon.svg';
import ProfileActiveIcon from '../../../common/assets/images/profileActiveIcon.svg';
import ProfileIcon from '../../../common/assets/images/profileIcon.svg';
import { PAGE_URL } from '../../constants/url';

interface NavItem {
  label: string;
  path: string;
  icon: string;
  activeIcon: string;
}

const NAV_ITEMS: NavItem[] = [
  {
    label: '홈',
    path: '/',
    icon: HomeIcon,
    activeIcon: HomeActiveIcon,
  },
  {
    label: '채팅',
    path: PAGE_URL.CHAT_ROOMS,
    icon: ChatIcon,
    activeIcon: ChatActiveIcon,
  },
  //   {
  //     label: '예약 관리',
  //     path: PAGE_URL.,
  //     icon: CalendarIcon,
  //     activeIcon: CalendarActiveIcon,
  //   },
  {
    label: '마이',
    path: PAGE_URL.MY_PAGE,
    icon: ProfileIcon,
    activeIcon: ProfileActiveIcon,
  },
];

function BottomNavigationBar() {
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <S_Container>
      {NAV_ITEMS.map((item) => {
        const isActive = location.pathname === item.path;

        return (
          <S_Item
            key={item.path}
            isActive={isActive}
            onClick={() => navigate(item.path)}
          >
            <S_IconWrapper isActive={isActive}>
              <S_Icon
                src={isActive && item.activeIcon ? item.activeIcon : item.icon}
                alt={item.label}
              />
            </S_IconWrapper>
            <S_Label isActive={isActive}>{item.label}</S_Label>
          </S_Item>
        );
      })}
    </S_Container>
  );
}

export default BottomNavigationBar;

const S_Container = styled.nav`
  display: flex;
  align-items: center;
  justify-content: space-around;
  position: fixed;
  bottom: 0;
  left: 50%;

  width: 48rem;
  height: 72px;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-top: 1px solid #e5e5e5;

  background: #fff;
  transform: translateX(-50%);

  @media screen and (width <= 480px) {
    width: 100%;
  }
`;
const S_Item = styled.button<{ isActive: boolean }>`
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  height: 100%;
  border: none;

  background: none;

  color: ${({ isActive, theme }) =>
    isActive ? theme.SYSTEM.MAIN500 : '#9ca3af'};
  cursor: pointer;
`;

const S_IconWrapper = styled.div<{ isActive: boolean }>`
  display: flex;
  align-items: center;
  justify-content: center;
`;

const S_Icon = styled.img`
  width: 24px;
  height: 24px;
`;

const S_Label = styled.span<{ isActive: boolean }>`
  margin-top: 4px;

  color: ${({ isActive, theme }) =>
    isActive ? theme.SYSTEM.MAIN500 : '#9ca3af'};
  font-weight: 500;
  font-size: 12px;
`;
