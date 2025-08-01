import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import useNameInput from '../../../../common/hooks/useNameInput';
import usePasswordInput from '../../hooks/usePasswordInput';
import useUserIdInput from '../../hooks/useUserIdInput';
import PasswordFields from '../PasswordFields/PasswordFields';
import PhoneFields from '../PhoneFields/PhoneFields';
import UserIdField from '../UserIdField/UserIdField';
import UserInfoFields from '../UserInfoFields/UserInfoFields';

function SignupForm() {
  const {
    name,
    handleNameChange,
    errorMessage: nameErrorMessage,
  } = useNameInput();

  const [gender, setGender] = useState('male');

  const handleGenderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setGender(e.target.value);
  };

  const {
    password,
    passwordConfirm,
    passwordErrorMessage,
    passwordConfirmErrorMessage,
    handlePasswordChange,
    handlePasswordConfirmChange,
  } = usePasswordInput();

  const {
    userId,
    handleUserIdChange,
    errorMessage: userIdErrorMessage,
  } = useUserIdInput();

  const isFormValid = () => {
    return false;
  };

  return (
    <StyledContainer>
      <StyledFormFields>
        <UserInfoFields
          name={name}
          nameErrorMessage={nameErrorMessage}
          handleNameChange={handleNameChange}
          gender={gender}
          handleGenderChange={handleGenderChange}
        />
        <UserIdField
          userId={userId}
          handleUserIdChange={handleUserIdChange}
          errorMessage={userIdErrorMessage}
        />
        <PasswordFields
          password={password}
          passwordConfirm={passwordConfirm}
          passwordErrorMessage={passwordErrorMessage}
          passwordConfirmErrorMessage={passwordConfirmErrorMessage}
          handlePasswordChange={handlePasswordChange}
          handlePasswordConfirmChange={handlePasswordConfirmChange}
        />
        <PhoneFields />
      </StyledFormFields>
      <Button
        variant={isFormValid() ? 'primary' : 'disabled'}
        type="submit"
        size="full"
        customStyle={css`
          height: 4.3rem;
          box-shadow: 0 4px 12px 0
            ${isFormValid() ? 'rgb(0 120 111 / 30%)' : 'rgb(0 0 0 / 8%)'};

          font-size: 1.6rem;
        `}
      >
        회원가입
      </Button>
    </StyledContainer>
  );
}

export default SignupForm;

const StyledContainer = styled.form`
  display: flex;
  flex-direction: column;
  gap: 3rem;

  padding: 2.8rem 3.3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledFormFields = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.7rem;
`;
