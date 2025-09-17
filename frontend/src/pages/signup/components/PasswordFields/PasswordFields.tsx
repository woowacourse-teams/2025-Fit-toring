import React, { useState } from 'react';

import styled from '@emotion/styled';

import blind from '../../../../common/assets/images/blind.svg';
import notBlind from '../../../../common/assets/images/notBlind.svg';
import FormField from '../../../../common/components/FormField/FormField';

interface PasswordFieldsProps {
  password: string;
  passwordConfirm: string;
  passwordErrorMessage: string;
  passwordConfirmErrorMessage: string;
  onPasswordChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onPasswordConfirmChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

function PasswordFields({
  password,
  passwordConfirm,
  passwordErrorMessage,
  passwordConfirmErrorMessage,
  onPasswordChange,
  onPasswordConfirmChange,
}: PasswordFieldsProps) {
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [passwordConfrimVisible, setPasswordConfrimVisible] = useState(false);

  return (
    <>
      <FormField label="비밀번호 *" errorMessage={passwordErrorMessage}>
        <S_InputWithIconWrapper>
          <S_Input
            id="password"
            name="password"
            placeholder="5자이상 15자이하 입력하세요"
            type={passwordVisible ? 'text' : 'password'}
            value={password}
            onChange={onPasswordChange}
          />
          <S_Img
            src={passwordVisible ? blind : notBlind}
            onClick={() => setPasswordVisible((prev) => !prev)}
          />
        </S_InputWithIconWrapper>
      </FormField>
      <FormField
        label="비밀번호 확인 *"
        errorMessage={passwordConfirmErrorMessage}
      >
        <S_InputWithIconWrapper>
          <S_Input
            id="passwordConfrim"
            name="passwordConfrim"
            placeholder="비밀번호를 다시 입력하세요"
            type={passwordConfrimVisible ? 'text' : 'password'}
            value={passwordConfirm}
            onChange={onPasswordConfirmChange}
          />
          <S_Img
            src={passwordConfrimVisible ? blind : notBlind}
            onClick={() => setPasswordConfrimVisible((prev) => !prev)}
          />
        </S_InputWithIconWrapper>
      </FormField>
    </>
  );
}

export default PasswordFields;

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
  border-radius: 7px;

  background-color: transparent;

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
