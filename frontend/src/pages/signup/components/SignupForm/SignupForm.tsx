import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import * as Sentry from '@sentry/react';

import Button from '../../../../common/components/Button/Button';
import useFormattedPhoneNumber from '../../../../common/hooks/useFormattedPhoneNumber';
import useNameInput from '../../../../common/hooks/useNameInput';
import useUserIdInput from '../../../../common/hooks/useUserIdInput';
import { getPhoneNumberErrorMessage } from '../../../../common/utils/phoneNumberValidator';
import { postSignup } from '../../apis/postSignup';
import usePasswordWithConfirmInput from '../../hooks/usePasswordWithConfirmInput';
import useUserIdDuplicateCheck from '../../hooks/useUserIdDuplicateCheck';
import useVerificationCodeConfirm from '../../hooks/useVerificationCodeConfirm';
import useVerificationCodeInput from '../../hooks/useVerificationCodeInput';
import useVerificationCodeRequest from '../../hooks/useVerificationCodeRequest';
import PasswordFields from '../PasswordFields/PasswordFields';
import PhoneFields from '../PhoneFields/PhoneFields';
import UserIdField from '../UserIdField/UserIdField';
import UserInfoFields from '../UserInfoFields/UserInfoFields';

import type { Gender, SignupInfo } from '../../types/signupInfo';

function SignupForm() {
  const {
    name,
    handleNameChange,
    errorMessage: nameErrorMessage,
    validated: nameValidated,
  } = useNameInput();

  const [gender, setGender] = useState<Gender>('남');

  const handleGenderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;

    const isGenderType = (value: string): value is SignupInfo['gender'] => {
      const genders = ['남', '여'];
      return genders.includes(value);
    };

    if (!isGenderType(value)) {
      return;
    }

    setGender(value);
  };

  const {
    userId,
    handleUserIdChange,
    errorMessage: userIdErrorMessage,
    validated: userIdValidated,
  } = useUserIdInput();

  const {
    duplicateError,
    handleDuplicateConfirmClick,
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
    passwordValidated,
    passwordConfrimValidated,
  } = usePasswordWithConfirmInput();

  const { phoneNumber, inputRef, handlePhoneNumberChange } =
    useFormattedPhoneNumber();

  const phoneNumberErrorMessage = getPhoneNumberErrorMessage(phoneNumber);

  const {
    shouldBlockSubmitByPhoneNumberCheck,
    handleAuthCodeClick,
    getFinalPhoneNumberErrorMessage,
    matchConfirmedPhoneNumber,
    requestCompleted,
  } = useVerificationCodeRequest({ phoneNumber, phoneNumberErrorMessage });

  const {
    verificationCode,
    handleVerificationCodeChange,
    errorMessage: verificationCodeErrorMessage,
    validated: verificationCodeValidated,
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
      nameValidated,
      userIdValidated && !duplicateError,
      passwordValidated,
      passwordConfrimValidated,
      phoneNumber !== '' && phoneNumberErrorMessage === '',
      verificationCodeValidated && !verificationCodeError,
    ];

    return validations.every(Boolean);
  };

  const getVerificationButtonEnabled = () => {
    return (
      matchConfirmedPhoneNumber &&
      phoneNumberErrorMessage === '' &&
      verificationCodeValidated &&
      requestCompleted
    );
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

    const invalidSignupInfo =
      !name ||
      (gender !== '남' && gender !== '여') ||
      !password ||
      !userId ||
      !phoneNumber;

    if (invalidSignupInfo) {
      return;
    }

    const signupInfo: SignupInfo = {
      name,
      loginId: userId,
      gender,
      phone: phoneNumber,
      password,
    };

    try {
      const response = await postSignup(signupInfo);
      if (response.status === 201) {
        alert('가입에 성공했습니다.');
      }
    } catch (error) {
      console.error('회원가입 실패', error);
      Sentry.captureException(error);
    }
  };

  return (
    <StyledContainer onSubmit={handleSubmit}>
      <StyledFormFields>
        <UserInfoFields
          name={name}
          nameErrorMessage={nameErrorMessage}
          onNameChange={handleNameChange}
          gender={gender}
          onGenderChange={handleGenderChange}
        />
        <UserIdField
          userId={userId}
          onUserIdChange={handleUserIdChange}
          onDuplicateConfrimClick={handleDuplicateConfirmClick}
          errorMessage={getFinalUserIdErrorMessage()}
          isUserIdInputValid={userIdErrorMessage === ''}
        />
        <PasswordFields
          password={password}
          passwordConfirm={passwordConfirm}
          passwordErrorMessage={passwordErrorMessage}
          passwordConfirmErrorMessage={passwordConfirmErrorMessage}
          onPasswordChange={handlePasswordChange}
          onPasswordConfirmChange={handlePasswordConfirmChange}
        />
        <PhoneFields
          phoneNumber={phoneNumber}
          verificationCode={verificationCode}
          verificationCodeErrorMessage={getFinalVerificationCodeErrorMessage()}
          phoneNumberErrorMessage={getFinalPhoneNumberErrorMessage()}
          onPhoneNumberChange={handlePhoneNumberChange}
          inputRef={inputRef}
          onVerificationCodeChange={handleVerificationCodeChange}
          onAuthCodeVerifyClick={handleAuthCodeVerifyClick}
          onAuthCodeClick={handleAuthCodeClick}
          isVerificationButtonEnabled={getVerificationButtonEnabled()}
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
