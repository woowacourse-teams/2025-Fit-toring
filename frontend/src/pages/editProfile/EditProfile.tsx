import styled from '@emotion/styled';

import useMyProfile from './hooks/useMyProfile';

function EditProfile() {
  const { myProfile } = useMyProfile();

  if (!myProfile) {
    return null;
  }

  return <S_Container>회원 정보 수정 페이지</S_Container>;
}

const S_Container = styled.section`
  background-color: ${({ theme }) => theme.BG.WHITE};
`;

export default EditProfile;
