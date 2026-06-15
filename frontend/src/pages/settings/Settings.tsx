import styled from '@emotion/styled';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { postLogout } from '../../common/apis/postLogout';
import chatIcon from '../../common/assets/images/chatIcon.svg';
import fittoringIconWithBg from '../../common/assets/images/fittoringIconWithBg.png';
import goIcon from '../../common/assets/images/goIcon.svg';
import settingsIcon from '../../common/assets/images/mypage-settings.svg';
import passwordIcon from '../../common/assets/images/notBlind.svg';
import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import { PAGE_URL } from '../../common/constants/url';
import { authCheckQueryOptions } from '../../common/queries/auth';
import { captureSentryError } from '../../common/utils/captureSentryError';
import {
  bootChannelTalk,
  shutdownChannelTalk,
} from '../../common/utils/channelTalk';
import MyPageSection from '../myPage/components/MyPageSection/MyPageSection';

import type { MyPageSectionItem } from '../myPage/components/MyPageSection/MyPageSection';

function Settings() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { logout } = useAuth();

  const handlePreparingClick = () => {
    window.alert('준비중입니다.');
  };

  const { mutate: handleLogout, isPending: logoutPending } = useMutation({
    mutationFn: postLogout,
    onSuccess: () => {
      shutdownChannelTalk();
      queryClient.removeQueries({ queryKey: authCheckQueryOptions.queryKey });
      logout();
      localStorage.removeItem('memberId');
      navigate(PAGE_URL.HOME);
    },
    onError: (error) => {
      console.error('Logout failed', error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'settings',
        step: 'logout',
      });
    },
  });

  const handleLogoutClick = () => {
    if (logoutPending) {
      return;
    }

    const confirmed = window.confirm('정말 로그아웃하시겠습니까?');

    if (!confirmed) {
      return;
    }

    handleLogout();
  };

  const handleInquiryClick = () => {
    bootChannelTalk();

    if (!window.ChannelIO) {
      window.alert('문의하기를 불러올 수 없습니다. 잠시 후 다시 시도해주세요.');
      return;
    }

    window.ChannelIO('showMessenger');
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
      onClick: () => navigate(PAGE_URL.APP_INSTALL_GUIDE),
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
      label: logoutPending ? '로그아웃 중...' : '로그아웃',
      onClick: handleLogoutClick,
    },
  ];

  const inquiryItems: MyPageSectionItem[] = [
    {
      iconSrc: chatIcon,
      label: '문의하기',
      onClick: handleInquiryClick,
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
