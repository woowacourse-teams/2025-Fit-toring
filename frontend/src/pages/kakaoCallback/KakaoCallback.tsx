import { useEffect } from 'react';

import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../common/constants/url';
import { captureSentryError } from '../../common/utils/captureSentryError';

import { postKakaoLogin } from './apis/postKakaoLogin';

function KakaoCallback() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const kakaoLoginMutation = useMutation({
    mutationFn: postKakaoLogin,
    onSuccess: (response) => {
      if (response.status === 200) {
        login();
        navigate(PAGE_URL.HOME);
      } else if (response.status === 204) {
        navigate(PAGE_URL.IDENTITY_VERIFICATION);
      } else {
        captureSentryError({
          error: new Error(`Unexpected response status: ${response.status}`),
          level: 'warning',
          feature: 'auth',
          step: 'kakao-login',
        });
        alert('로그인에 실패했습니다.');
        navigate(PAGE_URL.LOGIN);
      }
    },
    onError: (error) => {
      console.error('카카오 로그인 에러', error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'auth',
        step: 'kakao-login',
      });
      alert('로그인 중 오류가 발생했습니다.');
      navigate(PAGE_URL.LOGIN);
    },
  });

  const kakaoLoginMutationMutate = kakaoLoginMutation.mutate;

  useEffect(() => {
    const authCode = new URLSearchParams(window.location.search).get('code');

    if (authCode) {
      const handleLogin = async (authCode: string) => {
        kakaoLoginMutationMutate(authCode);
      };

      handleLogin(authCode);
    } else {
      alert('잘못된 접근입니다.');
      navigate(PAGE_URL.LOGIN);
    }
  }, [kakaoLoginMutationMutate, navigate]);

  return <LoadingSpinner />;
}

export default KakaoCallback;
