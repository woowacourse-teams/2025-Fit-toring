import styled from '@emotion/styled';

import chatIcon from '../../common/assets/images/chatIcon.svg';
import fittoringIconWithBg from '../../common/assets/images/fittoringIconWithBg.png';
import goIcon from '../../common/assets/images/goIcon.svg';
import settingsIcon from '../../common/assets/images/mypage-settings.svg';
import passwordIcon from '../../common/assets/images/notBlind.svg';
import MyPageSection from '../myPage/components/MyPageSection/MyPageSection';

import type { MyPageSectionItem } from '../myPage/components/MyPageSection/MyPageSection';

function Settings() {
  const handlePreparingClick = () => {
    window.alert('준비중입니다.');
  };

  const appItems: MyPageSectionItem[] = [
    {
      iconSrc: settingsIcon,
      label: '푸시 알림 설정',
      onClick: handlePreparingClick,
    },
    {
      iconSrc: fittoringIconWithBg,
      label: '앱 설치 안내',
      onClick: handlePreparingClick,
    },
  ];

  const accountItems: MyPageSectionItem[] = [
    {
      iconSrc: passwordIcon,
      label: '비밀번호 변경',
      onClick: handlePreparingClick,
    },
    {
      iconSrc: goIcon,
      label: '로그아웃',
      onClick: handlePreparingClick,
    },
  ];

  const inquiryItems: MyPageSectionItem[] = [
    {
      iconSrc: chatIcon,
      label: '문의하기',
      onClick: handlePreparingClick,
    },
  ];

  return (
    <S_Container>
      <MyPageSection items={appItems} title="앱 설정" />
      <MyPageSection items={accountItems} title="계정" />
      <MyPageSection divided={false} items={inquiryItems} title="문의" />
    </S_Container>
  );
}

export default Settings;

const S_Container = styled.main`
  display: flex;
  flex-direction: column;

  width: 100%;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
