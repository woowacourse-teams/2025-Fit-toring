import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../common/components/Button/Button';
import useNameInput from '../../../common/hooks/useNameInput';
import PasswordFields from '../../signup/components/PasswordFields/PasswordFields';
import UserInfoFields from '../../signup/components/UserInfoFields/UserInfoFields';
import usePasswordWithConfirmInput from '../../signup/hooks/usePasswordWithConfirmInput';
import useGender from '../hooks/useGender';

import type { UserProfileResponse } from '../types/userProfile';

interface EditProfileFormProps {
  myProfile: UserProfileResponse;
}

function EditProfileForm({ myProfile }: EditProfileFormProps) {
  const { name: initialName, gender: initialGender } = myProfile;
  const initialPassword = '';
  const initialPasswordConfirm = '';

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

  const profileFields = [
    {
      target: 'name',
      changed: name !== initialName,
      validated: nameValidated,
    },
    {
      target: 'gender',
      changed: gender !== initialGender,
      validated: !!gender,
    },
    {
      target: 'password',
      changed:
        password !== initialPassword ||
        passwordConfirm !== initialPasswordConfirm,
      validated: passwordValidated && passwordConfirmValidated,
    },
  ] as const;

  const myProfileChanged = profileFields.some((item) => item.changed);

  const validateForm = () => {
    if (!myProfileChanged) {
      return false;
    }

    const validations = profileFields
      .filter((item) => item.changed)
      .map((item) => item.validated);

    return validations.every(Boolean);
  };

  return (
    <S_Container>
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
