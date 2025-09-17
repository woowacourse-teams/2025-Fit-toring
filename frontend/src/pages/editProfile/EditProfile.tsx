import styled from '@emotion/styled';

function EditProfile() {
  return <S_Container>회원 정보 수정 페이지</S_Container>;
}

const S_Container = styled.div`
  padding-bottom: 3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

export default EditProfile;
