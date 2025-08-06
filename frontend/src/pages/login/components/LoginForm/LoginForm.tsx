import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import blind from '../../../../common/assets/images/blind.svg';
import notBlind from '../../../../common/assets/images/notBlind.svg';
import Button from '../../../../common/components/Button/Button';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';
import { postLogin } from '../../apis/postLogin';

function LoginForm() {
  const [passwordVisible, setPasswordVisible] = useState(false);

  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');

  const fetchLogin = async () => {
    try {
      const response = await postLogin(userId, password);
      if (response.status === 200) {
        alert('로그인에 성공했습니다.');
      }
    } catch (error) {
      console.error('로그인 실패', error);
    }
  };

  const handleUserIdChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserId(e.target.value);
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    fetchLogin();
  };

  const loginFormValidated = userId !== '' && password !== '';

  return (
    <StyledContainer onSubmit={handleSubmit}>
      <StyledFields>
        <FormField label="아이디">
          <StyledInputWrapper>
            <Input
              placeholder="fittoring"
              value={userId}
              onChange={handleUserIdChange}
              required
            />
          </StyledInputWrapper>
        </FormField>
        <FormField label="비밀번호" errorMessage={''}>
          <StyledInputWithIconWrapper>
            <StyledInput
              id="password"
              name="password"
              placeholder="••••••••"
              type={passwordVisible ? 'text' : 'password'}
              value={password}
              onChange={handlePasswordChange}
              required
            />
            <StyledImg
              src={passwordVisible ? blind : notBlind}
              onClick={() => setPasswordVisible((prev) => !prev)}
            />
          </StyledInputWithIconWrapper>
        </FormField>
      </StyledFields>
      <Button
        type="submit"
        size="full"
        customStyle={css`
          height: 4.3rem;
          box-shadow: 0 4px 12px 0 rgb(0 120 111 / 30%);
          font-size: 1.6rem;
        `}
        variant={loginFormValidated ? 'primary' : 'disabled'}
      >
        로그인
      </Button>
    </StyledContainer>
  );
}

export default LoginForm;

const StyledContainer = styled.form`
  display: flex;
  flex-direction: column;
  gap: 3.5rem;
`;

const StyledFields = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;
`;
const StyledInputWrapper = styled.div`
  height: 4rem;
`;

const StyledInputWithIconWrapper = styled.div<{ errored?: boolean }>`
  position: relative;
`;

const StyledInput = styled.input<{ errored?: boolean }>`
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

const StyledImg = styled.img`
  position: absolute;
  right: 0;
  bottom: 50%;
  width: 2rem;
  transform: translateY(50%);
  cursor: pointer;
  margin-right: 1rem;
`;
