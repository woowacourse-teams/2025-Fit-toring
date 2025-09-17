import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { getUserInfo } from '../../../../common/apis/getUserInfo';
import defaultProfile from '../../../../common/assets/images/profileImg.svg';
import { captureSentryError } from '../../../../common/utils/captureSentryError';

import type { UserInfo } from '../../../../common/types/userInfo';

function MyProfile() {
  const [myProfile, setMyProfile] = useState<UserInfo | null>(null);

  useEffect(() => {
    const fetchMyProfile = async () => {
      try {
        const response = await getUserInfo();
        setMyProfile(response);
      } catch (error) {
        console.error('Failed to fetch profile:', error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'myPage',
          step: 'fetch-profile',
        });
      }
    };
    fetchMyProfile();
  }, []);

  if (!myProfile) {
    return null;
  }

  const { loginId, name, phoneNumber, image } = myProfile;

  return (
    <S_Container>
      <S_Intro>멘토링 활동 내역을 확인하고 개인정보를 관리하세요.</S_Intro>
      <S_Wrapper>
        <S_Image src={image || defaultProfile} alt="내 프로필 이미지" />
        <S_Name>{name}</S_Name>
        <S_Id>아이디: {loginId}</S_Id>
        <S_Phone>전화번호: {phoneNumber}</S_Phone>
      </S_Wrapper>
    </S_Container>
  );
}

export default MyProfile;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;

  height: auto;
  padding: 2rem;
`;

const S_Intro = styled.h2`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;

  width: 100%;
  height: auto;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: rgb(0 0 0 / 10%) 0 0.4rem 1.2rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Image = styled.img`
  width: 6rem;
  height: 6rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN300};
  border-radius: 50%;
`;

const S_Name = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B1_B}
`;

const S_Id = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_Phone = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;
