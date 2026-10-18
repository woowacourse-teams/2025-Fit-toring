import styled from '@emotion/styled';

import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import EditProfileForm from './components/EditProfileForm';
import useMyProfile from './hooks/useMyProfile';

function EditProfile() {
  const { myProfile } = useMyProfile();

  const seo = (
    <SEO
      title="프로필 수정 - 핏토링 Fittoring"
      canonicalPath={PAGE_URL.EDIT_PROFILE}
    />
  );

  if (!myProfile) {
    return (
      <S_SpinnerContainer>
        {seo}
        <LoadingSpinner />
      </S_SpinnerContainer>
    );
  }

  return (
    <S_Container>
      {seo}
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
