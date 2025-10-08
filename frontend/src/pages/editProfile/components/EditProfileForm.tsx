import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import ApiError from '../../../common/apis/ApiError';
import Button from '../../../common/components/Button/Button';
import PhoneFields from '../../../common/components/PhoneFields/PhoneFields';
import UserInfoFields from '../../../common/components/UserInfoFields/UserInfoFields';
import { PAGE_URL } from '../../../common/constants/url';
import useFormattedPhoneNumber from '../../../common/hooks/useFormattedPhoneNumber';
import useNameInput from '../../../common/hooks/useNameInput';
import useVerificationCodeConfirm from '../../../common/hooks/useVerificationCodeConfirm';
import useVerificationCodeInput from '../../../common/hooks/useVerificationCodeInput';
import useVerificationCodeRequest from '../../../common/hooks/useVerificationCodeRequest';
import { captureSentryError } from '../../../common/utils/captureSentryError';
import { getPhoneNumberErrorMessage } from '../../../common/utils/phoneNumberValidator';
import PasswordFields from '../../signup/components/PasswordFields/PasswordFields';
import usePasswordWithConfirmInput from '../../signup/hooks/usePasswordWithConfirmInput';
import { patchMyProfile } from '../apis/patchMyProfile';
import useGender from '../hooks/useGender';
import useVerificationStep from '../hooks/useVerificationStep';

import type {
  PartialUserProfileRequest,
  UserProfileResponse,
} from '../types/userProfile';

interface EditProfileFormProps {
  myProfile: UserProfileResponse;
}

function EditProfileForm({ myProfile }: EditProfileFormProps) {
  const {
    name: initialName,
    gender: initialGender,
    phoneNumber: initialPhoneNumber,
  } = myProfile;
  const initialPassword = '';
  const initialPasswordConfirm = '';
  const initialVerificationCode = '';

  const {
    name,
    handleNameChange,
    errorMessage: nameErrorMessage,
    validated: nameValidated,
  } = useNameInput(initialName);

  const { gender, handleGenderChange } = useGender(initialGender);

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

  const {
    phoneNumber,
    inputRef,
    handlePhoneNumberChange: changePhoneNumber,
  } = useFormattedPhoneNumber(initialPhoneNumber);

  const {
    verificationStep,
    reset: resetVerification,
    request: requestVerification,
    complete: completeVerification,
  } = useVerificationStep('editProfile');

  const handlePhoneNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    changePhoneNumber(e);
    resetVerification();
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
    completeRequest: requestVerification,
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

  const verificationRequestButtonEnabled =
    phoneNumber !== initialPhoneNumber &&
    phoneNumberErrorMessage === '' &&
    phoneNumber !== '';

  const verificationButtonEnabled =
    phoneNumber !== initialPhoneNumber &&
    matchConfirmedPhoneNumber &&
    phoneNumberErrorMessage === '' &&
    verificationCodeValidated;

  const profileFields = [
    {
      target: 'name',
      changed: name !== initialName,
      validated: nameValidated,
      value: name,
    },
    {
      target: 'gender',
      changed: gender !== initialGender,
      validated: !!gender,
      value: gender,
    },
    {
      target: 'password',
      changed:
        password !== initialPassword ||
        passwordConfirm !== initialPasswordConfirm,
      validated: passwordValidated && passwordConfirmValidated,
      value: password,
    },
    {
      target: 'phoneNumber',
      changed: phoneNumber !== initialPhoneNumber,
      validated: phoneNumber !== '' && phoneNumberErrorMessage === '',
      value: phoneNumber,
    },
    {
      target: 'verificationCode',
      changed: verificationCode !== initialVerificationCode,
      validated: verificationCodeValidated && !verificationCodeError,
      value: verificationCode,
    },
  ] as const;

  const validateForm = () => {
    const myProfileChanged = profileFields.some((item) => item.changed);
    if (!myProfileChanged) {
      return false;
    }

    const validation = profileFields
      .filter((item) => item.changed)
      .every((item) => item.validated);

    return validation;
  };

  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (shouldBlockSubmitByVerificationCode()) {
      return;
    }

    if (
      shouldBlockSubmitByPhoneNumberCheck() &&
      initialPhoneNumber !== phoneNumber
    ) {
      return;
    }

    if (phoneNumber !== initialPhoneNumber && verificationStep !== 'verified') {
      setSubmitVerificationErrorMessage('인증을 다시 해주세요.');
      return;
    }

    const updatedUserProfile: PartialUserProfileRequest = profileFields
      .filter((item) => item.target !== 'verificationCode' && item.changed)
      .reduce((acc, cur) => {
        return { ...acc, [cur.target]: cur.value };
      }, {} as PartialUserProfileRequest);

    try {
      const response = await patchMyProfile(updatedUserProfile);
      if (response.status === 204) {
        alert('회원정보 수정에 성공했습니다.');
        navigate(PAGE_URL.HOME);
      }
    } catch (error) {
      console.error('회원정보 수정 실패', error);
      if (error instanceof ApiError) {
        alert(error.message);
      }

      captureSentryError({
        error,
        level: 'warning',
        feature: 'updatedUserProfile',
        step: 'updatedUserProfile',
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
          phoneNumberErrorMessage={
            initialPhoneNumber !== phoneNumber
              ? getFinalPhoneNumberErrorMessage()
              : ''
          }
          onPhoneNumberChange={handlePhoneNumberChange}
          inputRef={inputRef}
          onVerificationCodeChange={handleVerificationCodeChange}
          onAuthCodeVerifyClick={handleAuthCodeVerifyClick}
          onAuthCodeClick={handleAuthCodeClick}
          verificationButtonEnabled={verificationButtonEnabled}
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
        회원정보 수정
      </Button>
    </S_Container>
  );
}

export default EditProfileForm;

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
