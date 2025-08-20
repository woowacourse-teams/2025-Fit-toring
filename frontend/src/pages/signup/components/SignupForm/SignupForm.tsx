import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import Button from '../../../../common/components/Button/Button';
import { PAGE_URL } from '../../../../common/constants/url';
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
import { captureSentryError } from '../../../../common/utils/captureSentryError';

export type VerificationStep = 'idle' | 'requested' | 'verified';

function SignupForm() {
  const navigate = useNavigate();

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

  const {
    phoneNumber,
    inputRef,
    handlePhoneNumberChange: changePhoneNumber,
  } = useFormattedPhoneNumber();

  const [verificationStep, setVerificationStep] =
    useState<VerificationStep>('idle');

  const handlePhoneNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    changePhoneNumber(e);
    setVerificationStep('idle');
  };

  const completeRequest = () => {
    setVerificationStep('requested');
  };

  const completeVerification = () => {
    setVerificationStep('verified');
  };

  const phoneNumberErrorMessage = getPhoneNumberErrorMessage(phoneNumber);

  const {
    shouldBlockSubmitByPhoneNumberCheck,
    handleAuthCodeClick,
    getFinalPhoneNumberErrorMessage,
    matchConfirmedPhoneNumber,
  } = useVerificationCodeRequest({
    phoneNumber,
    phoneNumberErrorMessage,
    completeRequest,
  });

  const {
    verificationCode,
    handleVerificationCodeChange,
    errorMessage: verificationCodeErrorMessage,
    validated: verificationCodeValidated,
  } = useVerificationCodeInput();

  const [submitVerificationErrorMessage, setSubmitVerificationErrorMessage] =
    useState('');

  const {
    verificationCodeError,
    handleAuthCodeVerifyClick,
    getFinalVerificationCodeErrorMessage,
    shouldBlockSubmitByVerificationCode,
  } = useVerificationCodeConfirm({
    verificationCode,
    verificationCodeErrorMessage,
    completeVerification,
  });

  const getDisplayedVerificationErrorMessage = () => {
    if (verificationStep === 'verified') {
      return getFinalVerificationCodeErrorMessage();
    }

    return submitVerificationErrorMessage;
  };

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

  const verificationRequestButtonEnabled =
    phoneNumberErrorMessage === '' && phoneNumber !== '';

  const getVerificationButtonEnabled = () => {
    return (
      matchConfirmedPhoneNumber &&
      phoneNumberErrorMessage === '' &&
      verificationCodeValidated
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

    if (verificationStep !== 'verified') {
      setSubmitVerificationErrorMessage('인증을 다시 해주세요.');
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
        navigate(PAGE_URL.LOGIN);
      }
    } catch (error) {
      console.error('회원가입 실패', error);

      captureSentryError({
        error,
        level: 'warning',
        feature: 'signup',
        step: 'signup',
      });
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
          verificationCodeErrorMessage={getDisplayedVerificationErrorMessage()}
          phoneNumberErrorMessage={getFinalPhoneNumberErrorMessage()}
          onPhoneNumberChange={handlePhoneNumberChange}
          inputRef={inputRef}
          onVerificationCodeChange={handleVerificationCodeChange}
          onAuthCodeVerifyClick={handleAuthCodeVerifyClick}
          onAuthCodeClick={handleAuthCodeClick}
          verificationButtonEnabled={getVerificationButtonEnabled()}
          verificationRequestButtonEnabled={verificationRequestButtonEnabled}
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
