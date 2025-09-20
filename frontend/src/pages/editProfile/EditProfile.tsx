import styled from '@emotion/styled';

import EditProfileForm from './components/EditProfileForm';
import useMyProfile from './hooks/useMyProfile';

function EditProfile() {
  const { myProfile } = useMyProfile();

  if (!myProfile) {
    return null;
  }

  return (
    <S_Container>
      <EditProfileForm myProfile={myProfile} />
    </S_Container>
  );
}

const S_Container = styled.section`
  background-color: ${({ theme }) => theme.BG.WHITE};
`;

export default EditProfile;
