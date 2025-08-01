import styled from '@emotion/styled';

import profileImg from '../../../../common/assets/images/profileImg.svg';

function MyProfile() {
  // TODO: 서버에서 받아온 사용자 정보를 사용하도록 수정
  const { name, phone, id, img } = {
    name: '홍길동',
    phone: '010-1234-5678',
    id: 'honggildong',
    img: profileImg,
  };

  return (
    <StyledContainer>
      <h2>멘토링 활동 내역을 확인하고 개인정보를 관리하세요.</h2>
      <div>
        <img src={img} alt="Profile" />
        <p>이름: {name}</p>
        <p>전화번호: {phone}</p>
        <p>아이디: {id}</p>
      </div>
    </StyledContainer>
  );
}

export default MyProfile;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;

  height: 100%;
  padding: 2rem;
  border-radius: 8px;
`;
