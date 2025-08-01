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
      <StyledIntro>
        멘토링 활동 내역을 확인하고 개인정보를 관리하세요.
      </StyledIntro>
      <StyledWrapper>
        <StyledImage src={img} alt="Profile" />
        <StyledName>{name}</StyledName>
        <StyledId>아이디: {id}</StyledId>
        <StyledPhone>전화번호: {phone}</StyledPhone>
      </StyledWrapper>
    </StyledContainer>
  );
}

export default MyProfile;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;

  height: 100%;
  padding: 2rem;
`;

const StyledIntro = styled.h2`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;

  width: 100%;
  height: auto;
  max-height: 24rem;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: rgb(0 0 0 / 10%) 0 0.4rem 1.2rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledImage = styled.img`
  width: 6rem;
  height: 6rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN300};
  border-radius: 50%;
`;

const StyledName = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B1_B}
`;

const StyledId = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const StyledPhone = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;
