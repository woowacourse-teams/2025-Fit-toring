import styled from '@emotion/styled';

import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';

interface ProfileProps {
  profileImg: string | null;
  mentorName: string;
  categories: string[];
  ratingAverage: string;
  ratingCount: number;
}

function Profile({
  profileImg,
  mentorName,
  categories,
  ratingAverage,
  ratingCount,
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
        <S_Title>{mentorName}</S_Title>
        <TextWithIcon
          text={`${ratingAverage} (${ratingCount}개 리뷰)`}
          iconSrc={starIcon}
          iconName="별점"
        />
        <CategoryTags tagNames={categories} />
      </S_InfoWrapper>
    </S_Container>
  );
}

export default Profile;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2rem;

  width: 100%;
  margin-top: 2.3rem;
`;

const S_ProfileImg = styled.img`
  flex-shrink: 0;

  width: 12rem;
  height: 12rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN400};
  border-radius: 50%;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
`;

const S_Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
  color: ${({ theme }) => theme.FONT.B01}
`;
