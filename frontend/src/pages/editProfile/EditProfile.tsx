import styled from '@emotion/styled';

import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';

import EditProfileForm from './components/EditProfileForm';
import useMyProfile from './hooks/useMyProfile';

function EditProfile() {
  const { myProfile } = useMyProfile();

  if (!myProfile) {
    return (
      <S_SpinnerContainer>
        <LoadingSpinner />;
      </S_SpinnerContainer>
    );
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

const S_SpinnerContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: calc(100vh - 5.7rem);
`;

export default EditProfile;
