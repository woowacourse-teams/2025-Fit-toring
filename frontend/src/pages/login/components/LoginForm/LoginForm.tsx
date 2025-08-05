import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import blind from '../../../../common/assets/images/blind.svg';
import notBlind from '../../../../common/assets/images/notBlind.svg';
import Button from '../../../../common/components/Button/Button';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';

function LoginForm() {
  const [passwordVisible, setPasswordVisible] = useState(false);

  return (
    <StyledContainer>
      <StyledFields>
        <FormField label="아이디">
          <StyledInputWrapper>
            <Input placeholder="fittoring" />
          </StyledInputWrapper>
        </FormField>
        <FormField label="비밀번호 *" errorMessage={''}>
          <StyledInputWithIconWrapper>
            <StyledInput
              id="password"
              name="password"
              placeholder="5자이상 15자이하 입력하세요"
              type={passwordVisible ? 'text' : 'password'}
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
    0.1rem solid;
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
