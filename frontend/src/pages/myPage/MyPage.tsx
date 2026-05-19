import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../common/constants/url';
import useMyProfile from '../editProfile/hooks/useMyProfile';

import MyPageProfileSummary from './components/MyPageProfileSummary/MyPageProfileSummary';
import MyPageSection from './components/MyPageSection/MyPageSection';

import type { MyPageSectionItem } from './components/MyPageSection/MyPageSection';

function MyPage() {
  const navigate = useNavigate();
  const { myProfile } = useMyProfile();

  const displayName = myProfile?.name?.trim() || '회원';

  const handleEditProfileClick = () => {
    window.alert('준비중입니다.');
  };

  const handlePreparingClick = () => {
    window.alert('준비중입니다.');
  };

  const mentoringItems: MyPageSectionItem[] = [
    {
      label: '내가 개설한 멘토링',
      onClick: () => navigate(PAGE_URL.CREATED_MENTORING),
    },
    {
      label: '내가 참여한 멘토링',
      onClick: () => navigate(PAGE_URL.PARTICIPATED_MENTORING),
    },
  ];

  const activityItems: MyPageSectionItem[] = [
    {
      label: '내가 작성한 글',
      onClick: handlePreparingClick,
    },
  ];

  const settingItems: MyPageSectionItem[] = [
    {
      label: '설정',
      onClick: handleEditProfileClick,
    },
  ];

  return (
    <S_Container>
      <MyPageProfileSummary
        profileImg={myProfile?.image}
        name={displayName}
        onClick={handleEditProfileClick}
      />
      <MyPageSection items={mentoringItems} title="멘토링" />
      <MyPageSection items={activityItems} title="커뮤니티" />
      <MyPageSection items={settingItems} title="설정" />
    </S_Container>
  );
}

export default MyPage;

const S_Container = styled.main`
  display: flex;
  flex-direction: column;

  width: 100%;
  padding: 2rem 1.8rem 2.4rem;
`;
