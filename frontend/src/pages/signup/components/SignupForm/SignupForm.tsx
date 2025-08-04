import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import useFormattedPhoneNumber from '../../../../common/hooks/useFormattedPhoneNumber';
import useNameInput from '../../../../common/hooks/useNameInput';
import { getPhoneNumberErrorMessage } from '../../../../common/utils/phoneNumberValidator';
import { postSignup } from '../../apis/postSignup';
import usePasswordInput from '../../hooks/usePasswordInput';
import useUserIdDuplicateCheck from '../../hooks/useUserIdDuplicateCheck';
import useUserIdInput from '../../hooks/useUserIdInput';
import useVerificationCodeConfirm from '../../hooks/useVerificationCodeConfirm';
import useVerificationCodeInput from '../../hooks/useVerificationCodeInput';
import useVerificationCodeRequest from '../../hooks/useVerificationCodeRequest';
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

  const [gender, setGender] = useState('남');

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
    duplicateError,
    handleDuplicateConfrimClick,
    shouldBlockSubmitByUserId,
    getFinalUserIdErrorMessage,
  } = useUserIdDuplicateCheck({ userId, userIdErrorMessage });

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
    shouldBlockSubmitByPhoneNumberCheck,
    handleAuthCodeClick,
    getFinalPhoneNumberErrorMessage,
  } = useVerificationCodeRequest({ phoneNumber, phoneNumberErrorMessage });

  const {
    verificationCode,
    handleVerificationCodeChange,
    errorMessage: verificationCodeErrorMessage,
    isValid: isVerificationCodeValid,
  } = useVerificationCodeInput();

  const {
    verificationCodeError,
    handleAuthCodeVerifyClick,
    getFinalVerificationCodeErrorMessage,
    shouldBlockSubmitByVerificationCode,
  } = useVerificationCodeConfirm({
    verificationCode,
    verificationCodeErrorMessage,
  });

  const validateForm = () => {
    const validations = [
      isNameValid,
      isUserIdValid && !duplicateError,
      isPasswordValid,
      isPasswordConfrimValid,
      phoneNumber !== '' && phoneNumberErrorMessage === '',
      isVerificationCodeValid && !verificationCodeError,
    ];

    return validations.every(Boolean);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (shouldBlockSubmitByUserId()) {
      return;
    }

    if (shouldBlockSubmitByVerificationCode()) {
      return;
    }

    if (shouldBlockSubmitByPhoneNumberCheck()) {
      return;
    }

    const form = e.target as HTMLFormElement;
    const data = new FormData(form);

    const name = data.get('name');
    const gender = data.get('gender');
    const password = data.get('password');
    const loginId = data.get('id');
    const phone = data.get('phone');

    const signupInfo = {
      name,
      loginId,
      gender,
      phone,
      password,
    };

    try {
      const response = await postSignup(signupInfo);
      if (response.status === 201) {
        alert('가입에 성공했습니다.');
      }
    } catch (error) {
      console.error('회원가입 실패', error);
    }
  };

  return (
    <StyledContainer onSubmit={handleSubmit}>
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
          handleDuplicateConfrimClick={handleDuplicateConfrimClick}
          errorMessage={getFinalUserIdErrorMessage()}
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
          verificationCodeErrorMessage={getFinalVerificationCodeErrorMessage()}
          phoneNumberErrorMessage={getFinalPhoneNumberErrorMessage()}
          handlePhoneNumberChange={handlePhoneNumberChange}
          inputRef={inputRef}
          handleVerificationCodeChange={handleVerificationCodeChange}
          handleAuthCodeVerifyClick={handleAuthCodeVerifyClick}
          handleAuthCodeClick={handleAuthCodeClick}
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
