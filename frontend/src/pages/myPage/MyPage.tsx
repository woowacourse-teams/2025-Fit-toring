import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import calendarIcon from '../../common/assets/images/mypage-calendar.svg';
import documentIcon from '../../common/assets/images/mypage-document.svg';
import settingsIcon from '../../common/assets/images/mypage-settings.svg';
import usersIcon from '../../common/assets/images/mypage-users.svg';
import { PAGE_URL } from '../../common/constants/url';
import useMyProfile from '../editProfile/hooks/useMyProfile';

import ActivityStatistics from './components/ActivityStatistics/ActivityStatistics';
import MyPageProfileSummary from './components/MyPageProfileSummary/MyPageProfileSummary';
import MyPageSection from './components/MyPageSection/MyPageSection';

import type { MyPageSectionItem } from './components/MyPageSection/MyPageSection';
import type { MemberRole } from '../../common/types/userInfo';

const ROLE_LABEL: Record<MemberRole, string> = {
  MENTEE: '멘티',
  MENTOR: '멘토',
};

function MyPage() {
  const navigate = useNavigate();
  const { myProfile } = useMyProfile();

  const displayName = myProfile?.name?.trim() || '회원';
  const myRole = myProfile?.myRole ?? 'MENTOR';

  const handleEditProfileClick = () => {
    navigate(PAGE_URL.EDIT_PROFILE);
  };

  const handlePreparingClick = () => {
    window.alert('준비중입니다.');
  };

  const mentoringItems: MyPageSectionItem[] = [
    {
      iconSrc: calendarIcon,
      label: '개설한 멘토링',
      onClick: () => navigate(PAGE_URL.CREATED_MENTORING),
    },
    {
      iconSrc: usersIcon,
      label: '참여한 멘토링',
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
      onClick: handleEditProfileClick,
    },
  ];

  return (
    <S_Container>
      <MyPageProfileSummary
        profileImg={myProfile?.image}
        name={displayName}
        roleLabel={ROLE_LABEL[myRole]}
        onClick={handleEditProfileClick}
      />
      <ActivityStatistics />
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
