import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import { postKakaoLogin } from './apis/postKakaoLogin';
import { PAGE_URL } from '../../common/constants/url';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { API_ENDPOINTS } from '../../common/constants/apiEndpoints';

function KakaoCallback() {
  const navigate = useNavigate();
  const { login } = useAuth();

  useEffect(() => {
    const authCode = new URLSearchParams(window.location.search).get('code');

    if (authCode) {
      const handleLogin = async (authCode: string) => {
        try {
          const response = await postKakaoLogin(authCode);

          if (response.status === 200) {
            login();
            navigate(PAGE_URL.HOME);
          } else if (response.status === 204) {
            navigate(PAGE_URL.IDENTITY_VERIFICATION);
          } else {
            alert('로그인에 실패했습니다.');
            navigate(PAGE_URL.LOGIN);
          }
        } catch (error) {
          console.error('카카오 로그인 에러', error);
          alert('로그인 중 오류가 발생했습니다.');
          navigate(PAGE_URL.LOGIN);
        }
      };

      handleLogin(authCode);
    } else {
      alert('잘못된 접근입니다.');
      navigate(PAGE_URL.LOGIN);
    }
  }, [navigate, login]);

  return <LoadingSpinner />;
}

export default KakaoCallback;
