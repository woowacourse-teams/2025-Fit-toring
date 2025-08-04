import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import defaultProfile from '../../../../common/assets/images/profileImg.svg';
import { getMyProfile } from '../../apis/getMyProfile';

import type { MyProfile } from '../../types/myProfile';

const INITIAL_PROFILE: MyProfile = {
  name: '',
  phoneNumber: '',
  loginId: '',
  imageUrl: '',
} as const;

function MyProfile() {
  const [myProfile, setMyProfile] = useState<MyProfile>(INITIAL_PROFILE);
  const { name, phoneNumber, loginId, imageUrl } = myProfile;

  useEffect(() => {
    const fetchMyProfile = async () => {
      try {
        const response = await getMyProfile();
        setMyProfile(response);
      } catch (error) {
        console.error('Failed to fetch profile:', error);
      }
    };
    fetchMyProfile();
  }, []);

  return (
    <StyledContainer>
      <StyledIntro>
        멘토링 활동 내역을 확인하고 개인정보를 관리하세요.
      </StyledIntro>
      <StyledWrapper>
        <StyledImage src={imageUrl || defaultProfile} alt="내 프로필 이미지" />
        <StyledName>{name}</StyledName>
        <StyledId>아이디: {loginId}</StyledId>
        <StyledPhone>전화번호: {phoneNumber}</StyledPhone>
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
