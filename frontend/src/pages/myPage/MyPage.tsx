import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import calendarIcon from '../../common/assets/images/mypage-calendar.svg';
import documentIcon from '../../common/assets/images/mypage-document.svg';
import settingsIcon from '../../common/assets/images/mypage-settings.svg';
import usersIcon from '../../common/assets/images/mypage-users.svg';
import { PAGE_URL } from '../../common/constants/url';
import useMyProfile from '../editProfile/hooks/useMyProfile';

import MyPageProfileSummary from './components/MyPageProfileSummary/MyPageProfileSummary';
import MyPageSection from './components/MyPageSection/MyPageSection';

import type { MyPageSectionItem } from './components/MyPageSection/MyPageSection';

function MyPage() {
  const navigate = useNavigate();
  const { myProfile } = useMyProfile();

  const displayName = myProfile?.name?.trim() || '회원';
  const myRole = myProfile?.myRole;

  const handleEditProfileClick = () => {
    navigate(PAGE_URL.EDIT_PROFILE);
  };

  const handlePreparingClick = () => {
    window.alert('준비중입니다.');
  };

  const mentoringItems: MyPageSectionItem[] = [
    ...(myRole === 'MENTOR'
      ? [
          {
            iconSrc: calendarIcon,
            label: '운영하는 멘토링',
            badgeLabel: '멘토전용',
            onClick: () => navigate(PAGE_URL.CREATED_MENTORING),
          },
        ]
      : []),
    {
      iconSrc: usersIcon,
      label: '수강하는 멘토링',
      onClick: () => navigate(PAGE_URL.PARTICIPATED_MENTORING),
    },
  ];

  const activityItems: MyPageSectionItem[] = [
    {
      iconSrc: documentIcon,
      label: '작성한 글',
      onClick: handlePreparingClick,
    },
  ];

  const accountItems: MyPageSectionItem[] = [
    {
      iconSrc: settingsIcon,
      label: '설정',
      onClick: () => navigate(PAGE_URL.SETTINGS),
    },
  ];

  return (
    <S_Container>
      <MyPageProfileSummary
        profileImg={myProfile?.image}
        name={displayName}
        role={myRole}
        onClick={handleEditProfileClick}
      />
      <MyPageSection items={mentoringItems} title="멘토링" />
      <MyPageSection items={activityItems} title="커뮤니티" />
      <MyPageSection divided={false} items={accountItems} title="설정" />
    </S_Container>
  );
}

export default MyPage;

const S_Container = styled.main`
  display: flex;
  flex-direction: column;

  width: 100%;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
