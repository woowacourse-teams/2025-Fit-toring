import { useRef, useState } from 'react';

import styled from '@emotion/styled';

import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
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
  const [pullOffset, setPullOffset] = useState(0);
  const touchStartYRef = useRef<number | null>(null);
  const hasPulledRef = useRef(false);

  const handleImgClick = () => {
    if (hasPulledRef.current) {
      hasPulledRef.current = false;
      return;
    }

    setOpened(true);
    document.body.style.overflow = 'hidden';
  };

  const handleCloseClick = () => {
    setOpened(false);
    document.body.style.overflow = 'auto';
  };

  useEscapeKeyDown(handleCloseClick, opened);

  const handleTouchStart = (event: React.TouchEvent<HTMLDivElement>) => {
    if (window.scrollY > 0) {
      return;
    }

    touchStartYRef.current = event.touches[0].clientY;
    hasPulledRef.current = false;
  };

  const handleTouchMove = (event: React.TouchEvent<HTMLDivElement>) => {
    if (touchStartYRef.current === null || window.scrollY > 0) {
      return;
    }

    const touchY = event.touches[0].clientY;
    const pullDistance = touchY - touchStartYRef.current;

    if (pullDistance <= 0) {
      setPullOffset(0);
      return;
    }

    const nextOffset = Math.min(pullDistance * 0.55, 120);
    hasPulledRef.current = nextOffset > 8;
    setPullOffset(nextOffset);
  };

  const handleTouchEnd = () => {
    touchStartYRef.current = null;
    setPullOffset(0);
  };

  return (
    <S_Container
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
      onTouchEnd={handleTouchEnd}
      onTouchCancel={handleTouchEnd}
    >
      <S_ImageStage>
        <S_ProfileImg
          src={profileImg || defaultProfileImg}
          alt="멘토 프로필 이미지"
          onError={(e) => {
            e.currentTarget.src = defaultProfileImg;
          }}
          onClick={handleImgClick}
        />
      </S_ImageStage>
      <S_InfoWrapper pullOffset={pullOffset}>
        <S_DragHandle aria-hidden="true" />
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
          ariaLabel={`(${ratingCount})개의 리뷰, 평균 ${ratingAverage}점`}
          text={`${ratingAverage} (${ratingCount})`}
          iconSrc={starIcon}
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

const S_ImageStage = styled.div`
  overflow: hidden;

  width: 100%;
  height: min(52dvh, 42rem);

  background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_ProfileImg = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center top;
  cursor: pointer;
`;

const S_InfoWrapper = styled.div<{ pullOffset: number }>`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  position: relative;
  z-index: 1;

  margin-top: ${({ pullOffset }) => `${-9.6 + pullOffset / 10}rem`};
  padding: 1.2rem 2.7rem 2.2rem;
  border-radius: 2rem 2rem 0 0;
  box-shadow: 0 -0.8rem 2rem rgb(17 17 17 / 8%);

  background-color: ${({ theme }) => theme.BG.WHITE};

  transition: margin-top 0.18s ease-out;
`;

const S_DragHandle = styled.div`
  align-self: center;

  width: 4rem;
  height: 0.4rem;
  margin-bottom: 0.8rem;
  border-radius: 999px;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY300};
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
