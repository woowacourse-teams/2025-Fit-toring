import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { useAuth } from '../../../../common/components/AuthProvider/AuthProvider';
import Button from '../../../../common/components/Button/Button';
import PhoneFields from '../../../../common/components/PhoneFields/PhoneFields';
import UserInfoFields from '../../../../common/components/UserInfoFields/UserInfoFields';
import { PAGE_URL } from '../../../../common/constants/url';
import useFormattedPhoneNumber from '../../../../common/hooks/useFormattedPhoneNumber';
import useNameInput from '../../../../common/hooks/useNameInput';
import useVerificationCodeConfirm from '../../../../common/hooks/useVerificationCodeConfirm';
import useVerificationCodeInput from '../../../../common/hooks/useVerificationCodeInput';
import useVerificationCodeRequest from '../../../../common/hooks/useVerificationCodeRequest';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { getPhoneNumberErrorMessage } from '../../../../common/utils/phoneNumberValidator';
import { postIdentityVerification } from '../../apis/postIdentityVerification';
import {
  Gender,
  IdentityVerificationInfo,
} from '../types/IdentityVerificationInfo';

export type VerificationStep = 'idle' | 'requested' | 'verified';

function IdentityVerificationForm() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const {
    name,
    handleNameChange,
    errorMessage: nameErrorMessage,
    validated: nameValidated,
  } = useNameInput();

  const [gender, setGender] = useState<Gender>('남');

  const handleGenderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;

    const isGenderType = (
      value: string,
    ): value is IdentityVerificationInfo['gender'] => {
      const genders = ['남', '여'];
      return genders.includes(value);
    };

    if (!isGenderType(value)) {
      return;
    }

    setGender(value);
  };

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
    const errorMessage = getFinalVerificationCodeErrorMessage();

    if (verificationStep !== 'verified') {
      return errorMessage;
    }

    return errorMessage === '' ? submitVerificationErrorMessage : errorMessage;
  };

  const validateForm = () => {
    const validations = [
      nameValidated,
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

    const invalidIdentityVerificationInfo =
      !name || (gender !== '남' && gender !== '여') || !phoneNumber;

    if (invalidIdentityVerificationInfo) {
      return;
    }

    const userInfo = {
      name,
      gender,
      phone: phoneNumber,
    };

    try {
      const response = await postIdentityVerification(userInfo);
      if (response.status === 201) {
        alert('본인 인증이 완료되었습니다.');
        login();
        navigate(PAGE_URL.HOME);
      }
    } catch (error) {
      console.error('본인 인증 실패', error);

      captureSentryError({
        error,
        level: 'warning',
        feature: 'identityVerification',
        step: 'identityVerification',
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
        본인 인증 완료
      </Button>
    </S_Container>
  );
}

export default IdentityVerificationForm;

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
