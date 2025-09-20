import styled from '@emotion/styled';

import useNameInput from '../../../common/hooks/useNameInput';
import UserInfoFields from '../../signup/components/UserInfoFields/UserInfoFields';
import useGender from '../hooks/useGender';

import type { UserProfileResponse } from '../types/userProfile';

interface EditProfileFormProps {
  myProfile: UserProfileResponse;
}

function EditProfileForm({ myProfile }: EditProfileFormProps) {
  const { name: initialName, gender: initialGender } = myProfile;

  const {
    name,
    handleNameChange,
    errorMessage: nameErrorMessage,
    validated: nameValidated,
  } = useNameInput(initialName);

  const { gender, handleGenderChange } = useGender(initialGender);

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
      </S_FormFields>
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
