import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import ApiError from '../../../../common/apis/ApiError';
import { postAuthCode } from '../../../../common/apis/postAuthCode';
import { postAuthCodeVerify } from '../../../../common/apis/postAuthCodeVerify';
import Button from '../../../../common/components/Button/Button';
import PhoneFields from '../../../../common/components/PhoneFields/PhoneFields';
import UserInfoFields from '../../../../common/components/UserInfoFields/UserInfoFields';
import { PAGE_URL } from '../../../../common/constants/url';
import useFormattedPhoneNumber from '../../../../common/hooks/useFormattedPhoneNumber';
import useNameInput from '../../../../common/hooks/useNameInput';
import useUserIdInput from '../../../../common/hooks/useUserIdInput';
import useVerificationCodeInput from '../../../../common/hooks/useVerificationCodeInput';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { getPhoneNumberErrorMessage } from '../../../../common/utils/phoneNumberValidator';
import { postSignup } from '../../apis/postSignup';
import usePasswordWithConfirmInput from '../../hooks/usePasswordWithConfirmInput';
import useUserIdDuplicateCheck from '../../hooks/useUserIdDuplicateCheck';
import PasswordFields from '../PasswordFields/PasswordFields';
import UserIdField from '../UserIdField/UserIdField';

import type { Gender, SignupInfo } from '../../types/signupInfo';

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

  // 아이디
  const [userIdDuplicateChecked, setUserIdDuplicateChecked] = useState(false);

  const {
    userId,
    handleUserIdChange,
    errorMessage: userIdErrorMessage,
    validated: userIdValidated,
  } = useUserIdInput();

  const {
    duplicateError,
    handleDuplicateConfirmClick,
    resetDuplicateCheck,
    duplicateChecked,
  } = useUserIdDuplicateCheck({ userId, userIdErrorMessage });

  const onUserIdChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    handleUserIdChange(e);

    setUserIdDuplicateChecked(false);

    if (duplicateChecked) {
      resetDuplicateCheck();
    }
  };

  const onDuplicateConfirmClick = () => {
    setUserIdDuplicateChecked(true);
    handleDuplicateConfirmClick();
  };

  const {
    password,
    passwordConfirm,
    passwordErrorMessage,
    passwordConfirmErrorMessage,
    handlePasswordChange,
    handlePasswordConfirmChange,
    passwordValidated,
    passwordConfirmValidated,
  } = usePasswordWithConfirmInput();

  const [submitAttempted, setSubmitAttempted] = useState(false);

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

  const handleAuthCodeClick = async (phoneNumber: string) => {
    try {
      const response = await postAuthCode(phoneNumber);
      if (response.status === 201) {
        alert('인증요청 성공');
        completeRequest();
      }
    } catch (error) {
      console.error('인증요청 실패', error);

      captureSentryError({
        error,
        level: 'error',
        feature: 'sms',
        step: 'send-code',
      });
    }
  };

  const getFinalPhoneNumberErrorMessage = () => {
    if (submitAttempted && verificationStep === 'idle') {
      return '인증요청을 해주세요.';
    }

    return phoneNumberErrorMessage;
  };

  const {
    verificationCode,
    handleVerificationCodeChange,
    errorMessage: verificationCodeErrorMessage,
    validated: verificationCodeValidated,
  } = useVerificationCodeInput();

  const [verificationCodeError, setVerificationCodeError] = useState(false);

  const onVerificationCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    handleVerificationCodeChange(e);
    setVerificationCodeError(false);
  };

  const handleAuthCodeVerifyClick = async (phoneNumber: string) => {
    setVerificationCodeError(false);
    try {
      const response = await postAuthCodeVerify(phoneNumber, verificationCode);
      if (response.status === 200) {
        alert('인증 성공');
      }
    } catch (error) {
      setVerificationCodeError(true);
      console.error('인증 실패', error);

      captureSentryError({
        error,
        level: 'error',
        feature: 'sms',
        step: 'verify-code',
      });
    } finally {
      completeVerification();
    }
  };

  const getFinalVerificationCodeErrorMessage = () => {
    if (verificationCodeErrorMessage) {
      return verificationCodeErrorMessage;
    }

    if (verificationStep === 'verified' && verificationCodeError) {
      return '인증 실패';
    }

    if (submitAttempted && verificationStep === 'requested') {
      return '인증을 해주세요';
    }

    return '';
  };

  const getDisplayedVerificationErrorMessage = () => {
    const errorMessage = getFinalVerificationCodeErrorMessage();

    if (verificationStep !== 'verified') {
      return errorMessage;
    }

    return errorMessage;
  };

  const validateForm = () => {
    const validations = [
      nameValidated,
      userIdValidated && !duplicateError,
      passwordValidated,
      passwordConfirmValidated,
      phoneNumber !== '' && phoneNumberErrorMessage === '',
      verificationCodeValidated && !verificationCodeError,
    ];

    return validations.every(Boolean);
  };

  const verificationRequestButtonEnabled =
    phoneNumberErrorMessage === '' && phoneNumber !== '';

  const getVerificationButtonEnabled = () => {
    return (
      verificationStep === 'requested' ||
      (verificationStep === 'verified' &&
        phoneNumberErrorMessage === '' &&
        verificationCodeValidated)
    );
  };

  const getFinalUserIdErrorMessage = () => {
    if (userIdErrorMessage !== '') {
      return userIdErrorMessage;
    }

    if (duplicateError) {
      return '이미 사용중인 아이디입니다.';
    }

    if (!userIdDuplicateChecked && submitAttempted) {
      return '중복확인을 해주세요';
    }

    return userIdErrorMessage;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setSubmitAttempted(true);

    if (!userIdDuplicateChecked) {
      return;
    }

    if (verificationStep !== 'verified') {
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
      if (error instanceof ApiError) {
        alert(error.message);
      }

      captureSentryError({
        error,
        level: 'warning',
        feature: 'signup',
        step: 'signup',
      });
    }
  };

  return (
    <S_Container onSubmit={handleSubmit}>
      <S_FormFields>
        <UserInfoFields
          name={name}
          nameErrorMessage={nameErrorMessage}
          onNameChange={handleNameChange}
          gender={gender}
          onGenderChange={handleGenderChange}
        />
        <UserIdField
          userId={userId}
          onUserIdChange={onUserIdChange}
          onDuplicateConfrimClick={onDuplicateConfirmClick}
          errorMessage={getFinalUserIdErrorMessage()}
          isUserIdInputValid={userIdErrorMessage === ''}
          duplicateChecked={duplicateChecked}
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
          onVerificationCodeChange={onVerificationCodeChange}
          onAuthCodeVerifyClick={handleAuthCodeVerifyClick}
          onAuthCodeClick={handleAuthCodeClick}
          verificationButtonEnabled={getVerificationButtonEnabled()}
          verificationRequestButtonEnabled={verificationRequestButtonEnabled}
        />
      </S_FormFields>
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
    </S_Container>
  );
}

export default SignupForm;

const S_Container = styled.form`
  display: flex;
  flex-direction: column;
  gap: 3rem;

  padding: 2.8rem 3.3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_FormFields = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.7rem;
`;
