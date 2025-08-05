import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import useFormattedPhoneNumber from '../../../../common/hooks/useFormattedPhoneNumber';
import useNameInput from '../../../../common/hooks/useNameInput';
import { getPhoneNumberErrorMessage } from '../../../../common/utils/phoneNumberValidator';
import usePasswordInput from '../../hooks/usePasswordInput';
import useUserIdInput from '../../hooks/useUserIdInput';
import useVerificationCodeInput from '../../hooks/useVerificationCodeInput';
import PasswordFields from '../PasswordFields/PasswordFields';
import PhoneFields from '../PhoneFields/PhoneFields';
import UserIdField from '../UserIdField/UserIdField';
import UserInfoFields from '../UserInfoFields/UserInfoFields';

function SignupForm() {
  const {
    name,
    handleNameChange,
    errorMessage: nameErrorMessage,
    isValid: isNameValid,
  } = useNameInput();

  const [gender, setGender] = useState('male');

  const handleGenderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setGender(e.target.value);
  };

  const {
    userId,
    handleUserIdChange,
    errorMessage: userIdErrorMessage,
    isValid: isUserIdValid,
  } = useUserIdInput();

  const {
    password,
    passwordConfirm,
    passwordErrorMessage,
    passwordConfirmErrorMessage,
    handlePasswordChange,
    handlePasswordConfirmChange,
    isPasswordValid,
    isPasswordConfrimValid,
  } = usePasswordInput();

  const { phoneNumber, inputRef, handlePhoneNumberChange } =
    useFormattedPhoneNumber();

  const phoneNumberErrorMessage = getPhoneNumberErrorMessage(phoneNumber);

  const {
    verificationCode,
    handleVerificationCodeChange,
    errorMessage: verificationCodeErrorMessage,
    isValid: isVerificationCodeValid,
  } = useVerificationCodeInput();

  const validateForm = () => {
    const validations = [
      isNameValid,
      isUserIdValid,
      isPasswordValid,
      isPasswordConfrimValid,
      phoneNumber !== '' && phoneNumberErrorMessage === '',
      isVerificationCodeValid,
    ];

    return validations.every(Boolean);
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
        <PhoneFields
          phoneNumber={phoneNumber}
          verificationCode={verificationCode}
          verificationCodeErrorMessage={verificationCodeErrorMessage}
          phoneNumberErrorMessage={phoneNumberErrorMessage}
          handlePhoneNumberChange={handlePhoneNumberChange}
          inputRef={inputRef}
          handleVerificationCodeChange={handleVerificationCodeChange}
        />
      </StyledFormFields>
      <Button
        variant={validateForm() ? 'primary' : 'disabled'}
        type="submit"
        size="full"
        customStyle={css`
          height: 4.3rem;
          box-shadow: 0 4px 12px 0
            ${validateForm() ? 'rgb(0 120 111 / 30%)' : 'rgb(0 0 0 / 8%)'};

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
