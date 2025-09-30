import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import { postKakaoLogin } from './apis/postKakaoLogin';
import { PAGE_URL } from '../../common/constants/url';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';

function KakaoCallback() {
  return <LoadingSpinner />;
}

export default KakaoCallback;
