import { useEffect, useRef } from 'react';

import { useNavigate, useSearchParams } from 'react-router-dom';

import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../common/constants/url';
import { captureSentryError } from '../../common/utils/captureSentryError';

import { postKakaoLogin } from './apis/postKakaoLogin';

function KakaoCallback() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const didLoginRef = useRef(false);
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const authCode = searchParams.get('code');

    if (!authCode || didLoginRef.current) return;
    didLoginRef.current = true;

    const handleLogin = async (authCode: string) => {
      try {
        const response = await postKakaoLogin(authCode);

        if (response.status === 200) {
          login();
          navigate(PAGE_URL.HOME, { replace: true });
        } else if (response.status === 204) {
          navigate(PAGE_URL.IDENTITY_VERIFICATION, { replace: true });
        }
      } catch (error) {
        console.error('카카오 로그인 에러', error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'auth',
          step: 'kakao-login',
        });
        alert('로그인 중 오류가 발생했습니다.');
        navigate(PAGE_URL.LOGIN);
      }
    };

    handleLogin(authCode);
  }, [navigate]);

  return <LoadingSpinner />;
}

export default KakaoCallback;
