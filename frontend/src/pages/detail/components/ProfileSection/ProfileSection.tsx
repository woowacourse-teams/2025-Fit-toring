import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import Button from '../../../../common/components/Button/Button';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';
import useEscapeKeyDown from '../../../../common/hooks/useEscapeKeyDown';
import ProfileImageModal from '../ProfileImageModal/ProfileImageModal';

interface ProfileProps {
  profileImg: string | null;
  mentorName: string;
  categories: string[];
  ratingAverage: string;
  ratingCount: number;
  introduction: string;
  onCertificateShowButton: () => void;
}

function ProfileSection({
  profileImg,
  mentorName,
  categories,
  ratingAverage,
  ratingCount,
  introduction,
  onCertificateShowButton,
}: ProfileProps) {
  const [opened, setOpened] = useState(false);

  const handleImgClick = () => {
    setOpened(true);
    document.body.style.overflow = 'hidden';
  };

  const handleCloseClick = () => {
    setOpened(false);
    document.body.style.overflow = 'auto';
  };

  useEscapeKeyDown(handleCloseClick, opened);

  return (
    <S_Container>
      <S_ProfileImg
        src={profileImg || defaultProfileImg}
        alt="멘토 프로필 이미지"
        onError={(e) => {
          e.currentTarget.src = defaultProfileImg;
        }}
        onClick={handleImgClick}
      />
      <S_InfoWrapper>
        <S_InfoHeader>
          <S_Title>{mentorName}</S_Title>

          <S_MoveLink
            href="#certificate-section"
            onClick={onCertificateShowButton}
          >
            자격사항 보러가기
          </S_MoveLink>
        </S_InfoHeader>

        <TextWithIcon
          text={`${ratingAverage} (${ratingCount})`}
          iconSrc={starIcon}
          iconName="별점"
        />
        <CategoryTags tagNames={categories} />
        <S_Introduction>{introduction}</S_Introduction>
      </S_InfoWrapper>
      <ProfileImageModal
        opened={opened}
        imageSrc={profileImg || defaultProfileImg}
        onCloseClick={handleCloseClick}
      />
    </S_Container>
  );
}

export default ProfileSection;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  position: relative;

  width: 100%;
`;

const S_ProfileImg = styled.img`
  width: 100%;
  height: 43rem;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  cursor: pointer;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;

  padding: 2.2rem 2.7rem;
`;

const S_InfoHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const S_Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H1_B};
  color: ${({ theme }) => theme.FONT.B01};
`;

const S_MoveLink = styled.a`
  display: block;

  padding: 1rem 1.35rem;
  border: none;
  border: ${({ theme }) => `1px solid ${theme.SYSTEM.GRAY300}`};
  border-radius: 0.7rem;

  background-color: transparent;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  color: inherit;
  font-size: 1.6rem;
  cursor: pointer;
  text-decoration: none;
`;

const S_Introduction = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  color: ${({ theme }) => theme.FONT.B01};
`;
