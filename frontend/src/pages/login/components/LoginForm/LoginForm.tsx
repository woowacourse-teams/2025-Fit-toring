import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import blind from '../../../../common/assets/images/blind.svg';
import notBlind from '../../../../common/assets/images/notBlind.svg';
import { useAuth } from '../../../../common/components/AuthProvider/AuthProvider';
import Button from '../../../../common/components/Button/Button';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';
import { PAGE_URL } from '../../../../common/constants/url';
import usePasswordInput from '../../../../common/hooks/usePasswordInput';
import useUserIdInput from '../../../../common/hooks/useUserIdInput';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { postLogin } from '../../apis/postLogin';
import kakaoLoginIcon from '../../../../common/assets/images/kakao_login_large_wide.png';

function LoginForm() {
  const [passwordVisible, setPasswordVisible] = useState(false);

  const { userId, handleUserIdChange } = useUserIdInput();
  const { password, handlePasswordChange } = usePasswordInput();

  const [errorMessage, setErrorMessage] = useState('');

  const navigate = useNavigate();

  const { login } = useAuth();

  const REST_API_KEY = process.env.KAKAO_REST_API_KEY;
  const KAKAO_REDIRECT_URL = process.env.KAKAO_REDIRECT_URL;

  const KAKAO_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${KAKAO_REDIRECT_URL}&response_type=code`;

  const fetchLogin = async () => {
    try {
      const response = await postLogin(userId, password);
      if (response.status === 200) {
        alert('로그인에 성공했습니다.');
        navigate(PAGE_URL.HOME);
        login();
      }
    } catch (error) {
      console.error('로그인 실패', error);
      if (error instanceof Error) {
        setErrorMessage(error.message);
      }

      captureSentryError({
        error,
        level: 'warning',
        feature: 'login',
        step: 'login',
      });
    }
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    fetchLogin();
  };

  const handleSocialLogin = () => {
    window.location.href = KAKAO_URL;
  };

  const loginFormValidated = userId !== '' && password !== '';

  return (
    <S_Container onSubmit={handleSubmit}>
      <S_Fields>
        <FormField label="아이디">
          <S_InputWrapper>
            <Input
              placeholder="fittoring"
              value={userId}
              onChange={handleUserIdChange}
              required
            />
          </S_InputWrapper>
        </FormField>
        <FormField label="비밀번호">
          <S_InputWithIconWrapper>
            <S_Input
              id="password"
              name="password"
              placeholder="••••••••"
              type={passwordVisible ? 'text' : 'password'}
              value={password}
              onChange={handlePasswordChange}
              required
            />
            <S_Img
              src={passwordVisible ? blind : notBlind}
              onClick={() => setPasswordVisible((prev) => !prev)}
            />
          </S_InputWithIconWrapper>
        </FormField>
      </S_Fields>
      {errorMessage && <S_ErrorText>{errorMessage}</S_ErrorText>}
      <S_ButtonWrapper>
        <Button
          type="submit"
          size="full"
          customStyle={css`
            height: 4.3rem;

            font-size: 1.8rem;
          `}
          variant={loginFormValidated ? 'primary' : 'disabled'}
        >
          로그인
        </Button>
        <S_KakaoButton type="button" onClick={handleSocialLogin} />
      </S_ButtonWrapper>
    </S_Container>
  );
}

export default LoginForm;

const S_Container = styled.form`
  display: flex;
  flex-direction: column;
`;

const S_Fields = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;
`;
const S_InputWrapper = styled.div`
  height: 4rem;
`;

const S_InputWithIconWrapper = styled.div<{ errored?: boolean }>`
  position: relative;
`;

const S_Input = styled.input<{ errored?: boolean }>`
  width: 100%;
  height: 4rem;
  padding: 0.7rem 1.1rem;
  padding-right: 4rem;
  border: ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.OUTLINE.DARK}
    1px solid;
  border-radius: 0.7rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  :focus {
    outline: none;
    border: 2px solid ${({ theme }) => theme.SYSTEM.MAIN600};
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

const S_Img = styled.img`
  position: absolute;
  right: 0;
  bottom: 50%;

  width: 2rem;
  transform: translateY(50%);
  cursor: pointer;

  margin-right: 1rem;
`;

const S_KakaoButton = styled.button`
  width: 100%;
  height: 4.3rem;
  padding: 0.6rem 1.1rem;
  border: none;
  border-radius: 0.7rem;
  cursor: pointer;
  background-image: url(${kakaoLoginIcon});
  background-size: cover;
  background-position: center;
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;

  margin-top: 3rem;
`;

const S_ErrorText = styled.span`
  margin-top: 1rem;

  color: ${({ theme }) => theme.FONT.ERROR};

  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;
