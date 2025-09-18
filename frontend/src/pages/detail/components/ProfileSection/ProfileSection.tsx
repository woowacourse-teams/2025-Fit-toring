import { css } from '@emotion/react';
import styled from '@emotion/styled';

import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import Button from '../../../../common/components/Button/Button';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';

interface ProfileProps {
  profileImg: string | null;
  mentorName: string;
  categories: string[];
  ratingAverage: string;
  ratingCount: number;
  introduction: string;
}

function ProfileSection({
  profileImg,
  mentorName,
  categories,
  ratingAverage,
  ratingCount,
  introduction,
}: ProfileProps) {
  return (
    <S_Container>
      <S_ProfileImg
        src={profileImg || defaultProfileImg}
        alt="멘토 프로필 이미지"
        onError={(e) => {
          e.currentTarget.src = defaultProfileImg;
        }}
      />
      <S_InfoWrapper>
        <S_InfoHeader>
          <S_Title>{mentorName}</S_Title>
          <Button
            variant="newPrimary"
            customStyle={css`
              padding: 1rem 1.35rem;
            `}
          >
            <S_MoveLink href="#certificate-section">
              자격사항 보러가기
            </S_MoveLink>
          </Button>
        </S_InfoHeader>

        <TextWithIcon
          text={`${ratingAverage} (${ratingCount})`}
          iconSrc={starIcon}
          iconName="별점"
        />
        <CategoryTags tagNames={categories} />
        <S_Introduction>{introduction}</S_Introduction>
      </S_InfoWrapper>
    </S_Container>
  );
}

export default ProfileSection;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`;

const S_ProfileImg = styled.img`
  width: 100%;
  height: 43rem;
  aspect-ratio: 1 / 1;
  object-fit: cover;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  padding: 2.2rem 2.7rem;
  gap: 0.7rem;
`;

const S_InfoHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const S_Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H1_B};
  color: ${({ theme }) => theme.FONT.B01};
`;

const S_MoveLink = styled.a`
  text-decoration: none;
  color: inherit;
  display: block;
`;

const S_Introduction = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  color: ${({ theme }) => theme.FONT.B01};
`;
